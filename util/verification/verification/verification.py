import json
from typing import Union
from hashlib import sha256
import ecdsa
import ecdsa.util


def verify(information_json_filename: str, signature_json_filename: str) -> bool:
    message = read_text_file(information_json_filename)
    signatures = list(read_json_file(signature_json_filename))
    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            verified = verify_ecdsa_with_sha256(
                message=message,
                signature_hex=signature['signature'],
                public_key_hex=signature['publicKey']
            )
            if not verified:
                return False
    return True


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


def read_text_file(filename: str) -> str:
    with open(filename) as opened:
        text = opened.read()
    return text


def read_json_file(filename: str) -> Union[dict, list]:
    with open(filename) as opened:
        data = json.load(opened)
    return data
