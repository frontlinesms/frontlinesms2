# Usage

## Building

Just call `mvn clean package`.

## Running from command line
Explode your WAR file to `webapps` directory, then call

    mvn exec:exec

and watch the magic happen.

# Known Issues
* restarting the server on Linux is bad - RXTX complains about classloader issues, and will then fail to load and attempt to fall back to javax.serial.
