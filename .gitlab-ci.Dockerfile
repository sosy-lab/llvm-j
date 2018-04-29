# This is a Docker image for running the tests.
# It should be pushed to registry.gitlab.com/sosy-lab/software/llvm-j/test
# and will be used by CI as declared in .gitlab-ci.yml.
#
# Commands for updating the image:
# docker build -t registry.gitlab.com/sosy-lab/software/llvm-j/test - < .gitlab-ci.Dockerfile
# docker push registry.gitlab.com/sosy-lab/software/llvm-j/test

FROM openjdk:8-jdk-slim
RUN apt-get update -q && apt-get install -qq ant llvm-3.9-dev g++-6 chrpath
