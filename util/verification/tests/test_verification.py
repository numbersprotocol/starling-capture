from verification import __version__
from verification import generate_sha256_from_filepath
from verification import get_signature_from_information_file
from verification import verify
from verification import verify_ethereum_signature


INFORMATION_FILENAME = './tests/assets/information.json'
SIGNATURE_FILENAME = './tests/assets/signature.json'
SIGNER_WALLET_ADDRESS = '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'


def test_version():
    assert __version__ == '0.1.0'


def test_verify():
    assert verify(INFORMATION_FILENAME, SIGNATURE_FILENAME)


def test_verify_ethereum_signature():
    message = generate_sha256_from_filepath(INFORMATION_FILENAME)
    signature = get_signature_from_information_file(SIGNATURE_FILENAME)
    recovered_wallet_address = verify_ethereum_signature(message, signature)
    assert SIGNER_WALLET_ADDRESS == recovered_wallet_address

