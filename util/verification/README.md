# Verification Tool

## Getting Start

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