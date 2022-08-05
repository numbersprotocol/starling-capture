from verification import __version__
from verification import generate_sha256_from_filepath
from verification import get_signature_from_information_file
from verification import hex_string_to_bytes
from verification import read_json_file
from verification import read_text_file
from verification import verify
from verification import verify_ecdsa_with_sha256
from verification import verify_ethereum_signature


INFORMATION_FILENAME = './tests/assets/information.json'
SIGNATURE_FILENAME = './tests/assets/signature.json'
SIGNER_WALLET_ADDRESS = '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'


def test_version():
    assert __version__ == '0.1.0'


def test_verify_ecdsa_with_sha256():
    signatures = list(read_json_file(SIGNATURE_FILENAME))
    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            message = read_text_file(INFORMATION_FILENAME)
            assert verify_ecdsa_with_sha256(
                message=message,
                signature_hex=signature['signature'],
                public_key_hex=signature['publicKey']
            )


def test_verify_ethereum_signature():
    message = generate_sha256_from_filepath(INFORMATION_FILENAME)
    signature = get_signature_from_information_file(SIGNATURE_FILENAME)
    recovered_wallet_address = verify_ethereum_signature(message, signature)
    assert SIGNER_WALLET_ADDRESS == recovered_wallet_address


def test_verify_legacy_zion_signature():
    ASSET_SHA256='ede8c1b820a19e0a438ce28873501fafd33886a21cf4e3c1ffea24f523c857c4'
    INFORMATION_FILENAME = f'./tests/assets/zion-legacy/{ASSET_SHA256}/information.json'
    SIGNATURE_FILENAME = f'./tests/assets/zion-legacy/{ASSET_SHA256}/signature.json'
    SIGNER_WALLET_ADDRESS = '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'

    sha256sum = generate_sha256_from_filepath(INFORMATION_FILENAME)
    message = hex_string_to_bytes(sha256sum)
    signature = get_signature_from_information_file(SIGNATURE_FILENAME)
    recovered_wallet_address = verify_ethereum_signature(message, signature)
    assert SIGNER_WALLET_ADDRESS == recovered_wallet_address


def test_verify():
    INFORMATION_FILENAME = f'./tests/assets/information.json'
    SIGNATURE_FILENAME = f'./tests/assets/signature.json'
    SIGNER_WALLET_ADDRESS = '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'
    verify(INFORMATION_FILENAME, SIGNATURE_FILENAME, SIGNER_WALLET_ADDRESS)
