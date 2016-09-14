# Contributing

When contributing to this repository, please: 

1. **Discuss** the change you wish to make via an **issue** with the owners of this repository **before making any change**.
2. [**Fork**](https://help.github.com/articles/fork-a-repo/) the repository _(if not already done)_
3. Do the work in a specific branch of your repository
4. Once the work is done, please [rebase your work](https://git-scm.com/docs/git-rebase) onto the last version of **develop** of the _original_ repository
5. Do a **pull request** and wait for the owners of this repository to accept/discuss it and merge it.

## Issue process

First of all, [fill an issue](https://github.com/xee-lab/xee-sdk-android/issues/new).

The issue **must** follow this guidelines:

|Field|Guidelines|
|---|---|
|name|Pattern should be like `[Group] Issue title`|
|description|Content should be as **complete as possible** !\*\*|

The _GROUP_ can be one of these items : 

* `FEAT` Related to a new **feature** (enhancement)
* `FIX` Related to a **bug** fix in the project
* `MISC` Related to something **about** the project, not a feature not a bug
* `DOC` Related to **documentation** about something

_For example: `[DOC] Typo in Readme` or `[FIX] Trip parser fails in some cases`_ 

\** Feel free to add pieces of `code` or [links]() to the problematic file. Even screenshots if you think it could be useful.

![Well documented issue](res/issue.png)

## Fork/Branch process

> If you want to fix the issue by yourself, please tell us in the conversation

Once accepted, if you didn't already fork the repository, please do it.

1. Update **your develop** branch to the last state of the _original_ repository **develop**  branch
2. Create a branch from this one. `git checkout -b your_branch`
3. [Do it !](http://i.giphy.com/87xihBthJ1DkA.gif)

![Fork branch tree](res/fork_branch.png)

## Rebase process

When you were making changes, it's possible that someone did some changes.
Changes already merged in master.

It's also possible that you made 42 commits where it might need 5. (Are commits like `foo`, `boooh` or `tada` important ?)

So the idea is to [rebase your work on the master branch](https://git-scm.com/docs/git-rebase) so you can:

* be up to date
* rewrite the history of what you've done.

Once done, please _push_ the rebased work on your fork!

|Before rebase|After rebase|
|---|---|
|![Before rebase](res/before_rebase.png)|![Before rebase](res/after_rebase.png)|

> Note that I used `reword` and `push --force` to update the branch on remote.

## Pull Request Process

Everything is done properly.
Please make a [pull request](https://help.github.com/articles/using-pull-requests/) from your branch to our **develop** branch.

The PR **must** follow this guidelines:

|Field|Guidelines|
|---|---|
|name|`Fixes #issue : issue title`|
|description|What have been done to resolve the issue|

_For example: `Fixes #1 : [DOC] Add images in CONTRIBUTING file`_ 

![Pull request example](res/pr.png)