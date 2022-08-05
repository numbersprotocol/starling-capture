import json

from hashlib import sha256
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

    def show(self):
        result = 'Pass' if self.sw_key_verification or self.hw_key_verification or self.hw_key_verification_classic else 'Fail'
        print(f'Verification result: {result}\n')

        print('Summary:')
        print(f'\tSW key verification: {self.sw_key_verification}')
        print(f'\tHW key verification (Zion): {self.hw_key_verification}')
        print(f'\tHW key verification classic (Zion): {self.hw_key_verification_classic}')
        print('\nNote: For the HW key verifications, only one of them will be True.')


def verify(information_json_filename: str,
             signature_json_filename: str,
             signer_wallet_address: str):
    '''
    '''
    verification_summary = VerificationSummary()
    signatures = list(read_json_file(signature_json_filename))

    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            message = read_text_file(information_json_filename)
            verification_summary.sw_key_verification = verify_ecdsa_with_sha256(
                message=message,
                signature_hex=signature['signature'],
                public_key_hex=signature['publicKey']
            )
        elif signature['provider'] == 'Zion':
            information_sha256sum = generate_sha256_from_filepath(information_json_filename)
            signature = get_signature_from_information_file(signature_json_filename)

            # Zion verirication
            message = information_sha256sum
            recovered_wallet_address = verify_ethereum_signature(message, signature)
            verification_summary.hw_key_verification = (recovered_wallet_address == signer_wallet_address)

            # Zion verirication classic
            message_classic = hex_string_to_bytes(information_sha256sum)
            recovered_wallet_address_classic = verify_ethereum_signature(message_classic, signature)
            verification_summary.hw_key_verification_classic = (recovered_wallet_address_classic == signer_wallet_address)
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
      print('encode_defunct, case primitive')
      encoded_message = encode_defunct(message)
    elif message[:2] != '0x':
      print('encode_defunct, case text')
      encoded_message = encode_defunct(text=message)
    else:
      print('encode_defunct, case hexstr')
      encoded_message = encode_defunct(hexstr=message)
    return w3.eth.account.recover_message(encoded_message, signature=signature)


def read_text_file(filename: str) -> str:
    with open(filename) as opened:
        text = opened.read()
    return text


def read_json_file(filename: str) -> Union[dict, list]:
    with open(filename) as opened:
        data = json.load(opened)
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
