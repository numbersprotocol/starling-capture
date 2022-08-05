from verification import __version__
from verification import generate_sha256_from_filepath
from verification import get_signature_from_information_file
from verification import verify_ethereum_signature
from verification import hex_string_to_bytes


ASSET_SHA256='ede8c1b820a19e0a438ce28873501fafd33886a21cf4e3c1ffea24f523c857c4'
INFORMATION_FILENAME = f'./tests/assets/zion-legacy/{ASSET_SHA256}/information.json'
SIGNATURE_FILENAME = f'./tests/assets/zion-legacy/{ASSET_SHA256}/signature.json'
SIGNER_WALLET_ADDRESS = '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'


def test_verify_legacy_zion_signature():
    sha256sum = generate_sha256_from_filepath(INFORMATION_FILENAME)
    print(f'sha256sum: {sha256sum}')
    message = hex_string_to_bytes(sha256sum)
    #message = sha256sum
    print(f'message: {message} {type(message)}')

    signature = get_signature_from_information_file(SIGNATURE_FILENAME)

    recovered_wallet_address = verify_ethereum_signature(message, signature)
    print(f'Signer wallet: {recovered_wallet_address}')
    assert SIGNER_WALLET_ADDRESS == recovered_wallet_address

