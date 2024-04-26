# What is this?

This is small function to generate API keys to be used by the main WeatherApp
software. The function simply takes a user defined api key and encrypts it
using AES encryption.

To use this function run it as main and give it two arguments (argv). The first
argument must be a valid location of an API key in JSON format with the fields
'id' and 'key'. The second argument is path, where you want the software to
write the encrypted file to. e.g.:
```
> KeyGen <api key read location> <encrypted api key write location>
```
The given api key file must be of the format:

```json5
{
  id: "custom key id or name for identifying",
  key: "api key value here as string"
}
```
