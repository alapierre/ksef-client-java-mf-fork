export JRELEASER_MAVENCENTRAL_USERNAME=...
export JRELEASER_MAVENCENTRAL_TOKEN=...

````shell
./gradlew :ksef-client:publishMavenPublicationToStagingRepository
./gradlew jreleaserConfig
./gradlew jreleaserDeploy
````