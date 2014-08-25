# Gradle JasperReports Plugin Release Notes

These notes describe the release process currently in use.

## Releasing

Release version with `release` plugin:

    gradle clean release

Checkout the appropriate tag:

    git checkout $version

Optionally publish locally and use a test project (future release will include integration tests):

    gradle clean build publishToMavenLocal
    cd ../gradle-jasperreports-test
    gradle clean compileAllReports
    cd -

## Publishing

Upload to Bintray:

    gradle bintrayUpload -PbintrayUser=$bintrayUser -PbintrayKey=$bintrayKey

Sign the version using the REST API:

    curl --basic --user $bintrayUser:$bintrayKey \
      https://api.bintray.com/gpg/gmazelier/maven/com.github.gmazelier:jasperreports-gradle-plugin/versions/$version \
      -X POST -H 'X-GPG-PASSPHRASE: $gpgPrivateKey'

Connect to [Bintray](https://bintray.com):

* review package,
* publish it,
* include it in [`jcenter`](http://jcenter.bintray.com),
* include it in [`gradle-plugins`](https://bintray.com/gradle/gradle-plugins).
