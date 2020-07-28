# Verification Tool

## Getting Started

### Environment

* Visual Studio Code
* Poetry 1.0.10 or later

### Installation

Install the dependencies with poetry.

``` bash
poetry install --no-root
```

Poetry will create the virtual environment in `.venv/` folder. Visual Studio Code will automatically select the corresponding Python interpreter with the `.vscode/settings.json` file. No manual configuration required.

``` json
{
    "python.pythonPath": ".venv/bin/python"
}
```

If you are using the built-in terminal in Visual Studio Code, remember to restart it by killing the terminal first in order to enter the virtual shell session automatically.

## Verify!

Currently, this tool only verify the digital signatures generated from Android OpenSSL provider.

To verify the json files generated from the Starling Capture app,

``` python
try:
    result = verify('./tests/assets/information.json', './tests/assets/signature.json')
except Error as e:
    print(e)
    result = False
```

See the [tests](./tests/test_verification.py) for details.

## Test

To run tests,

``` bash
pytest
```