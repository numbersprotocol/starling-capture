from verification import __version__, verify

INFORMATION_FILENAME = './tests/assets/information.json'
SIGNATURE_FILENAME = './tests/assets/signature.json'


def test_version():
    assert __version__ == '0.1.0'


def test_verify():
    assert verify(INFORMATION_FILENAME, SIGNATURE_FILENAME)
