# Copyright 2016 Eliocity
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/bin/sh
# this hook is used to check the unit test before pushing anything.
#
# It is automatically linked when the command `make install` has been ran.
#
# to skip the tests (a thing you don't want to), run with the --no-verify argument
#       i.e. - $ 'git push --no-verify'

echo "pre-push started (Run unit test before pushing anything)"

# stash any unstaged changes
git stash -q --keep-index

# run the tests with the gradle wrapper
make check

# store the last exit code in a variable
RESULT=$?

# unstash the unstashed changes
git stash pop -q

echo "pre-commit ended"

# return the './gradlew testSandboxDebugUnitTest' exit code
exit $RESULT