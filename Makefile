.PHONY: help check test clean doc

help:
	@echo "----- INSTALL -------------------------------------------------------------------"
	@echo "install              install and link the scripts for the project"
	@echo "----- BUILD ---------------------------------------------------------------------"
	@echo "all                  clean and build the project"
	@echo "build                build the project in all modes"
	@echo "buildDebug           build the project for the sandbox in debug mode"
	@echo "buildRelease         build the project for the production in release mode"
	@echo "----- TESTS && LINT -------------------------------------------------------------"
	@echo "test                 test all packages (mock mode)"
	@echo "lint                 lint all packages"
	@echo "----- OTHERS --------------------------------------------------------------------"
	@echo "clean                clean the project"
	@echo "help                 print this message"
	@echo "doc                  build the javadoc"

all: clean build

install:
	chmod +x ./scripts/pre-push-test.sh
	ln -sf ../../scripts/pre-push-test.sh ./.git/hooks/pre-push
	chmod +x ./.git/hooks/pre-push
	chmod +x ./scripts/pre-commit-test.sh
	ln -sf ../../scripts/pre-commit-test.sh ./.git/hooks/pre-commit
	chmod +x ./.git/hooks/pre-commit

check:
	./gradlew :sdk-core:lintRelease :sdk-api:lintNetworkRelease :sdk-api:testMockReleaseUnitTest

test:
	./gradlew testMockDebugUnitTest

lint:
	./gradlew lint

clean:
	./gradlew clean

build:
	./gradlew :sdk-core:assembleRelease :sdk-api:assembleNetworkRelease

doc:
	./gradlew javadoc
