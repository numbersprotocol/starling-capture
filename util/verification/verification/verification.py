import json
import sys

from hashlib import sha256
from sha3 import keccak_256
from typing import Union

import ecdsa
import ecdsa.util

from eth_account.messages import encode_defunct
from web3.auto import w3

import verification


class VerificationSummary(object):
    def __init__(self):
        self.sw_key_verification = False

        self.hw_key_verification = False
        self.hw_key_verification_classic = False

        self.hw_session_key_verification = False
        self.hw_session_key_verification_classic = False

        self.signer_wallet = None
        self.recovered_wallet = None
        self.recovered_wallet_classic = None

    def show(self):
        result = 'Pass' if (
            self.sw_key_verification or
            self.hw_key_verification or
            self.hw_key_verification_classic or
            self.hw_session_key_verification or
            self.hw_session_key_verification_classic) else 'Fail'
        print(f'Verification result: {result}\n')

        print('Summary:')
        print(f'\tSW key verification: {self.sw_key_verification}')
        if self.signer_wallet != None:
            print(f'\tHW key verification (Zion): {self.hw_key_verification}')
            print(f'\tHW key verification classic (Zion): {self.hw_key_verification_classic}')
            print(f'\tHW session key verification (Zion): {self.hw_session_key_verification}')
            print(f'\tHW session key verification classic (Zion): {self.hw_session_key_verification_classic}')

            print('\nWallet Addresses:')
            print(f'\tZion singer wallet address: {self.signer_wallet.lower()}')
            if self.recovered_wallet:
                print(f'\tRecovered wallet address: {self.recovered_wallet.lower()}')
            if self.recovered_wallet_classic:
                print(f'\tRecovered wallet address classic: {self.recovered_wallet_classic.lower()}')

            print('\nNote')
            print('\t1. For the HW-related verifications, only one of them will be True.')
        else:
            print(f'\tHW key verification: No HW signature')


def verify(information_json_filename: str, signature_json_filename: str):
    '''
    '''
    verification_summary = VerificationSummary()

    try:
        signatures = list(read_json_file(signature_json_filename))
    except TypeError as e:
        print('CRITICAL: Cannot read signature.json correctly. Please double check if the provided signature.json is valid.')
        sys.exit(1)

    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            try:
                message = read_text_file(information_json_filename)
            except TypeError as e:
                print('CRITICAL: Cannot read information.json correctly. Please double check if the provided information.json is valid.')
                sys.exit(1)
            try:
                verification_summary.sw_key_verification = verify_ecdsa_with_sha256(
                    message=message,
                    signature_hex=signature['signature'],
                    public_key_hex=signature['publicKey']
                )
            except Exception as e:
                print(f'{e}')
                verification_summary.sw_key_verification = False
        elif signature['provider'] == 'Zion':
            parsed_public_keys = parse_zion_public_key_from_signature(signature['publicKey'])
            zion_signer_wallet_address = public_key_to_wallet_address(parsed_public_keys['send'])
            verification_summary.signer_wallet = zion_signer_wallet_address

            # The signature is Zion signature (not session-based)
            if len(signature['signature']) == 130:
                information_sha256sum = generate_sha256_from_filepath(information_json_filename)
                signature = get_signature_from_information_file(signature_json_filename)

                # Zion verirication
                message = information_sha256sum
                recovered_wallet_address = verify_ethereum_signature(message, signature)
                verification_summary.hw_key_verification = (recovered_wallet_address.lower() == zion_signer_wallet_address.lower())
                verification_summary.recovered_wallet = recovered_wallet_address

                # Zion verirication classic
                message_classic = hex_string_to_bytes(information_sha256sum)
                recovered_wallet_address_classic = verify_ethereum_signature(message_classic, signature)
                verification_summary.hw_key_verification_classic = (recovered_wallet_address_classic.lower() == zion_signer_wallet_address.lower())
                verification_summary.recovered_wallet_classic = recovered_wallet_address_classic
            # The signature is Zion session-based classic signature
            elif 'Session' in signature['publicKey'] and 'SessionSignature' not in signature['publicKey']:
                message = read_text_file(information_json_filename)
                session_public_key = signature['publicKey'].split('\n')[1]
                try:
                    verification_summary.hw_session_key_verification_classic = verify_ecdsa_with_sha256(
                        message=message,
                        signature_hex=signature['signature'],
                        public_key_hex=session_public_key
                    )
                except Exception as e:
                    print(f'{e}')
                    verification_summary.hw_session_key_verification_classic = False
            # The signature is Zion session-based signature
            elif 'Session' in signature['publicKey'] and 'SessionSignature' in signature['publicKey']:
                message = read_text_file(information_json_filename)
                session_public_key = signature['publicKey'].split('\n')[1]

                session_sw_key_verification = False
                session_hw_key_verification = False

                try:
                    session_sw_key_verification = verify_ecdsa_with_sha256(
                        message=message,
                        signature_hex=signature['signature'],
                        public_key_hex=session_public_key
                    )
                except Exception as e:
                    print(f'{e}')
                    session_sw_key_verification = False

                # verify if session public key is signed by Zion signer
                session_public_key_zion_signature = parsed_public_keys['sessionsignature']
                message = sha256(session_public_key.encode('utf-8')).hexdigest()
                signature = '0x' + session_public_key_zion_signature
                recovered_wallet_address = verify_ethereum_signature(message, signature)

                session_hw_key_verification = True if recovered_wallet_address.lower() == zion_signer_wallet_address else False
                verification_summary.hw_session_key_verification = session_sw_key_verification and session_hw_key_verification
                verification_summary.recovered_wallet = recovered_wallet_address
            else:
                print(f'ERROR: Unknown signature with length {len(signature["signature"])}, skip')
        else:
            print(f'ERROR: Unknown provider: {signature["provider"]}, skip')
    verification_summary.show()


def verify_ecdsa_with_sha256(message: str, signature_hex: str, public_key_hex: str) -> bool:
    public_key_bytes = bytes.fromhex(public_key_hex)
    public_key = ecdsa.VerifyingKey.from_der(
        public_key_bytes,
        hashfunc=sha256
    )
    return public_key.verify(
        bytes.fromhex(signature_hex),
        message.encode('utf-8'),
        sigdecode=ecdsa.util.sigdecode_der
    )


def verify_ethereum_signature(message, signature):
    """Verify Ethereum-compatible signature (EIP-191).

    Zion's Ethereum signature follows EIP-191.

    Args:
    ¦   message: raw content for signing and verifying
    ¦   signature: signature of the raw content (message above) with leading '0x'

    Returns:
    ¦   str: recovered (Signer's) Ethereum wallet address
    """
    if type(message) is not str:
      encoded_message = encode_defunct(message)
    elif message[:2] != '0x':
      encoded_message = encode_defunct(text=message)
    else:
      encoded_message = encode_defunct(hexstr=message)
    return w3.eth.account.recover_message(encoded_message, signature=signature)


def read_text_file(filename: str) -> str:
    try:
        with open(filename) as opened:
            text = opened.read()
    except Exception as e:
        print(e)
        text = None
    return text


def read_json_file(filename: str) -> Union[dict, list]:
    try:
        with open(filename) as opened:
            data = json.load(opened)
    except Exception as e:
        print(e)
        data = None
    return data


def generate_sha256_from_filepath(filepath: str) -> str:
    """Calculate SHA256 of the given file.

    Args:
    ¦   filepath (str): the filepath of the targeting file

    Returns:
    ¦   str: sha256 in hex string
    """
    with open(filepath, 'rb') as f:
        raw_bytes = f.read()
        return sha256(raw_bytes).hexdigest()


def get_signature_from_information_file(filepath: str) -> str:
    signatures = list(read_json_file(filepath))
    for signature in signatures:
        if signature['provider'] == 'Zion':
            signature_hex = '0x' + signature['signature']
            return signature_hex
    return ''


def hex_string_to_bytes(hex_string):
    return bytes.fromhex(hex_string)


def public_key_to_wallet_address(eth_public_key):
    '''Calculate Ethereum wallet address from Zion's compressed public key

    The Zion public key is compressed public key (66-byte). The high-level steps are
    1. Compute the uncompressed public key.
    2. Get keccak256 hash of the uncompressed public key.
    3. Get the last 20-byte to be the wallet address.
    '''
    vk = ecdsa.keys.VerifyingKey.from_string(bytes.fromhex(eth_public_key), curve=ecdsa.curves.SECP256k1)
    # strip the leading 04
    public_key_bytes = vk.to_string('uncompressed')[1:]
    wallet_address = '0x' + keccak_256(public_key_bytes).digest()[-20:].hex()
    return wallet_address


def parse_zion_public_key_from_signature(signature_publickey_value: str):
    '''Parse the value of the publicKey field in the signature.json to a dict.
    '''
    items = list(filter(None, signature_publickey_value.split('\n')))
    keys = [i.replace(':', '').lower() for i in items[::2]]
    values = items[1::2]
    return dict(zip(keys, values))


if __name__ == '__main__':
    # preparation
    sha256sum = '06e4c344ac75d3176e6ad94434e45440761094aa51673919ea3f1805eb7e655a'
    msg = hex_string_to_bytes(sha256sum)
    private_key = b"\xb2\\}\xb3\x1f\xee\xd9\x12''\xbf\t9\xdcv\x9a\x96VK-\xe4\xc4rm\x03[6\xec\xf1\xe5\xb3d"

    # sign
    message = encode_defunct(msg)
    signed_message = w3.eth.account.sign_message(message, private_key=private_key)
    print(f'Signed message: {signed_message}')

    # verify
    signer_wallet = w3.eth.account.recover_message(message, signature=signed_message.signature)
    print(f'Signer wallet: {signer_wallet}')

    # generate Ethereum wallet address from Zion compressed public key
    compressed_public_key = '03aced43f9dddc120291f5cdf73580fbb592b5b21054ce61eb73cbaf98efcbe82e'
    public_key_to_wallet_address(compressed_public_key)

    public_key = "Session:\n3059301306072a8648ce3d020106082a8648ce3d03010703420004f749217e283ee7d09b1fddd2c7b23cb9a54dabca82af243c5587810aab5967dd74678581d845715e3f309e681a254da21492a2b09e0b0478157b7302408e875b\n\nReceive:\n03aced43f9dddc120291f5cdf73580fbb592b5b21054ce61eb73cbaf98efcbe82e\n\nSend:\n03aced43f9dddc120291f5cdf73580fbb592b5b21054ce61eb73cbaf98efcbe82e"
    parsed_public_keys = parse_zion_public_key_from_signature(public_key)
    signer_wallet = public_key_to_wallet_address(parsed_public_keys['send'])
    print(f'Parsed public keys: {parsed_public_keys}')
    print(f'Zion signer wallet: {signer_wallet}')