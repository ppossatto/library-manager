# TLS Certification for local development

> Note: this can **only** be applied for local development, for other environments is required a more adequate 
> certificate authority (CA)

## Download

In order to generate the TLS certificate for local development, you must firstly install **mkcert**:

### Linux

To install mkcert on Linux devices, you can run the following based on your distro:

#### Arch

```bash
sudo pacman -Syu mkcert
sudo pacman -Syu libnss3-tools
```

#### Debian/Ubuntu

```bash
sudo apt install mkcert
sudo apt install libnss3-tools
```

#### RHEL/Fedora

```bash
sudo dnf install mkcert
sudo dnf install nss-tools
```

#### Install the binaries directly

```bash
curl -LO "https://dl.filippo.io/mkcert/latest?for=linux/amd64"
chmod +x mkcert-v*-linux-amd64
sudo cp mkcert-v*-linux-amd64 /usr/local/bin/mkcert
```

#### Homebrew in Linux

```bash
brew install mkcert
brew install nss
```

### Windows

For installing mkcert on Windows, there are two options:

#### Install with Chocolatey

```bash
choco install mkcert
```

#### Install with WinGet (PowerShell)

```bash
winget install -e --id FiloSottile.mkcert
```

### Mac

It's the same way as Homebrew in Linux:

```bash
brew install mkcert
brew install nss
```



## Installation

When mkcert and NSS (for TLS on Firefox) are downloaded in your machine, run the following:

```bash
mkcert -install
```

When completed, open the terminal in the project root directory and run the following:

```bash
mkcert localhost 127.0.0.1
```

This will create two files in the project root:

- localhost+1.pem
- localhost+1-key.pem

Don't worry, they will not leave your machine since it's already defined in the .gitignore file (please don't remove it)

After the files are generated, run the following so Spring can understand the certificate (still on project root):

```bash
openssl pkcs12 -export \
  -in localhost+1.pem \
  -inkey localhost+1-key.pem \
  -out keystore.p12 \
  -name library-manager \
  -passout pass:changeit # You can change the password, but you also need to change on application.yml
```

After that, a file named "keystore.p12" will be generated, so move it to the "src/main/resources" folder:

With that, the backend will have TLS encryption **only** for local development.