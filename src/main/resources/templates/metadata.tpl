<metadata>
  <groupId>${group/'.'}</groupId>
  <artifactId>${name}</artifactId>
  <version>${latest}</version>
  <versioning>
    <latest>${latest}</latest>
${release*metadata_release}
${snapshot*metadata_snapshot}
    <versions>
${versions*metadata_version/}
    </versions>
  <lastUpdated>${stamp}</lastUpdated>
  </versioning>
</metadata>