from verification import __version__
from verification import generate_sha256_from_filepath
from verification import get_signature_from_information_file
from verification import hex_string_to_bytes
from verification import read_json_file
from verification import read_text_file
from verification import verify
from verification import verify_ecdsa_with_sha256
from verification import verify_ethereum_signature


Testing_captures = {
    'sw_key_signature': {
        'information_filename': './tests/assets/zion/9f9af13673a0ea1e608fdd4c6ce6c746dd03dd58d184705ea7d1e1ef175e5640/information.json',
        'signature_filename': './tests/assets/zion/9f9af13673a0ea1e608fdd4c6ce6c746dd03dd58d184705ea7d1e1ef175e5640/signature.json',
        'signer_wallet_address': '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'
    },
    'hw_key_signature': {
        'information_filename': './tests/assets/zion/9f9af13673a0ea1e608fdd4c6ce6c746dd03dd58d184705ea7d1e1ef175e5640/information.json',
        'signature_filename': './tests/assets/zion/9f9af13673a0ea1e608fdd4c6ce6c746dd03dd58d184705ea7d1e1ef175e5640/signature.json',
        'signer_wallet_address': '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'
    },
    'hw_key_signature_classic': {
        'information_filename': './tests/assets/zion-classic/ede8c1b820a19e0a438ce28873501fafd33886a21cf4e3c1ffea24f523c857c4/information.json',
        'signature_filename': './tests/assets/zion-classic/ede8c1b820a19e0a438ce28873501fafd33886a21cf4e3c1ffea24f523c857c4/signature.json',
        'signer_wallet_address': '0x5f5AD77F4f924232a6E486216Ddefba8a732b96B'
    }
}


def test_sw_key_verification():
    print('test_sw_key_verification')

    capture = Testing_captures['sw_key_signature']
    signatures = list(read_json_file(capture['signature_filename']))

    for signature in signatures:
        if signature['provider'] == 'AndroidOpenSSL':
            message = read_text_file(capture['information_filename'])
            print(f'\tmessage: {message}, signature: {signature["signature"]}')
            assert verify_ecdsa_with_sha256(
                message=message,
                signature_hex=signature['signature'],
                public_key_hex=signature['publicKey']
            )


def test_hw_key_verification():
    print('test_hw_key_verification')
    capture = Testing_captures['hw_key_signature']
    message = generate_sha256_from_filepath(capture['information_filename'])
    signature = get_signature_from_information_file(capture['signature_filename'])
    print(f'\tmessage: {message}, signature: {signature}')
    recovered_wallet_address = verify_ethereum_signature(message, signature)
    assert capture['signer_wallet_address'] == recovered_wallet_address


def test_hw_key_verification_classic():
    print('test_hw_key_verification_classic')
    capture = Testing_captures['hw_key_signature_classic']
    sha256sum = generate_sha256_from_filepath(capture['information_filename'])
    message = hex_string_to_bytes(sha256sum)
    signature = get_signature_from_information_file(capture['signature_filename'])
    print(f'\tmessage: {message}, signature: {signature}')
    recovered_wallet_address = verify_ethereum_signature(message, signature)
    assert capture['signer_wallet_address'] == recovered_wallet_address


def test_verify():
    print('test_verify')
    capture = Testing_captures['hw_key_signature']
    verify(
        capture['information_filename'],
        capture['signature_filename'],
        capture['signer_wallet_address']
    )
