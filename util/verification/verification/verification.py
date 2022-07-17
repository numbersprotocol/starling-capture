import json

from hashlib import sha256
from typing import Union

import ecdsa
import ecdsa.util

from eth_account.messages import encode_defunct
from web3.auto import w3


def verify(information_json_filename: str, signature_json_filename: str) -> bool:
    message = read_text_file(information_json_filename)
    signatures = list(read_json_file(signature_json_filename))
    all_verified = True
    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            verified = verify_ecdsa_with_sha256(
                message=message,
                signature_hex=signature['signature'],
                public_key_hex=signature['publicKey']
            )
            print(f'provider: AndroidOpenSSL, verified: {verified}')
            if not verified:
                all_verified = False
        #elif signature['provider'] == 'Zion':
        #    parsed_public_key = signature['publicKey'].split('\n')
        #    print(f'Parsed public key: {parsed_public_key}')
        #    verified = verify_ecdsa_with_sha256(
        #        message=message,
        #        signature_hex=signature['signature'],
        #        public_key_hex=signature['publicKey']
        #    )
        #    if not verified:
        #        all_verified = False
        else:
            # unknown provider
            print(f'provider: {signature["provider"]}, skip')
            #all_verified = False
    return all_verified


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


def verify_ethereum_signature(message: str, signature: str) -> str:
    """Verify Ethereum-compatible signature

    Args:
    ¦   message (str): the signed message
    ¦   signature (str): the signature generated from the signed message with leading '0x'

    Returns:
    ¦   str: the recovered Ethereum wallet address
    """
    encoded_message = encode_defunct(text=message)
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
