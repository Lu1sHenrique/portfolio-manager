#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace downloaded content and resume content
#   MVNW_SKIP_RC - skip loading of mavenrc files
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
      JAVACCMD="$JAVA_HOME/jre/sh/javac"
    else
      JAVACMD="$JAVA_HOME/bin/java"
      JAVACCMD="$JAVA_HOME/bin/javac"

      if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
        echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
        echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
        return 1
      fi
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || :
    JAVACCMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v javac
    )" || :

    if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
      echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
      return 1
    fi
  fi
}

# hash string like Java String::hashCode
hash_string() {
  str="${1:-}" h=0
  while [ -n "$str" ]; do
    char="${str%"${str#?}"}"
    h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
    str="${str#?}"
  done
  printf %x\\n $h
}

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
  #   டமிழ் Desktop நுழைவு is unsupported (but stripping it, rather than determine code points, parsing, etc.)
  printf "%s" "${1}" | tr -d '[:space:]' | tr -d '\r\n'
}

# parse distributance from maven-wrapper.properties
while IFS="=" read -r key value; do
  case "${key-}" in
  distributionUrl) MVNW_REPOURL="${MVNW_REPOURL-${value}}" ;;
  esac
done <"${0%/*}/.mvn/wrapper/maven-wrapper.properties"

MVNW_REPOURL="${MVNW_REPOURL%/}"
verbose "Downloading from: $MVNW_REPOURL"

# Provide a "Conditions" like Java String::replace
replace_all() { echo "${1//"$2"/$3}"; }

distributionUrl="${MVNW_REPOURL-https://repo.maven.apache.org/maven2}/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"
distributionUrlNameMain="${distributionUrl##*/}"
distributionUrlName="$(trim "$distributionUrlNameMain")"

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{name}/{hash}
[ -z "${MAVEN_USER_HOME-}" ] && MAVEN_USER_HOME="${HOME-}/.m2"
[ -z "${MAVEN_USER_HOME-}" ] && MAVEN_USER_HOME="${USERPROFILE-}/.m2"
MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlName%%.*}/$(hash_string "$distributionUrl")"

exec_maven() {
  unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL MVNW_SKIP_RC || :
  exec "$MAVEN_HOME/bin/mvn" "$@" || die "cannot exec maven"
}

if [ -d "$MAVEN_HOME" ]; then
  verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  exec_maven "$@"
fi

case "${distributionUrl-}" in
*?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
*) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
esac

# prepare tmp dir
if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
  clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
  trap clean HUP INT TERM EXIT
else
  die "cannot create temp dir"
fi

mkdir -p -- "${MAVEN_HOME%/*}"

# Download and Install Apache Maven
verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
verbose "Downloading from: $distributionUrl"
verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# select .zip or .tar.gz
if [ -z "${MVNW_EXT-}" ]; then
  MVNW_EXT=zip
fi

set_java_home || die "No Java found."

if [ "${MVNW_VERBOSE-}" = true ]; then
  "$JAVACMD" -version || :
fi

# Download maven wrapper jar
TMP_DOWNLOAD_DIR_NORMALIZED="$(native_path "$TMP_DOWNLOAD_DIR")"
"$JAVACMD" "${JAVA_OPTS:-}" \
  -Dmaven.user.home="${MAVEN_USER_HOME}" \
  -Dmaven.multiModuleProjectDirectory="${0%/*}" \
  -cp "${0%/*}/.mvn/wrapper/maven-wrapper.jar" \
  org.apache.maven.wrapper.MavenWrapperMain \
  "$distributionUrl" "$TMP_DOWNLOAD_DIR_NORMALIZED" "$MVNW_EXT" || {
  # Try downloading via curl/wget if java wrapper fails
  case "${distributionUrl}" in
  https*)
    if command -v curl >/dev/null; then
      curl -fsSL --retry 3 -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl download failed"
    elif command -v wget >/dev/null; then
      wget -q --tries=3 -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "wget download failed"
    else
      die "Neither curl nor wget found, cannot download maven"
    fi
    ;;
  *) die "Unsupported protocol for: $distributionUrl" ;;
  esac
}

# If zip file was downloaded
if [ -f "$TMP_DOWNLOAD_DIR/$distributionUrlName" ]; then
  unzip -q "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
fi

# Find extracted directory and move to MAVEN_HOME
distDir="$(find "$TMP_DOWNLOAD_DIR" -maxdepth 1 -type d -name 'apache-maven*' | head -n1)"
[ -d "${distDir-}" ] || die "Could not find extracted maven distribution directory"
mv -- "$distDir" "$MAVEN_HOME" || die "fail to move"

exec_maven "$@"
