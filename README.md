# Clay

![Clay logo](resources/Clay.svg)


Clay is a small Clojure tool for a dynamic workflow of data visualization and literate programming.

[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/clay.svg)](https://clojars.org/org.scicloj/clay)

## Usage

Add [org.scicloj/clay](https://clojars.org/org.scicloj/clay) as a dependency. 
  * (If you are building a library and using Clay to document it, probably you do not need Clay to be a dependency of the library, but only of your dev environment, so you may define it as an extra dependency in a Deps alias. See, for example, the [deps.edn](https://github.com/scicloj/noj/blob/main/deps.edn) file of [Noj](https://github.com/scicloj/noj).)

Setup and usage can be found in the [Documentation](https://scicloj.github.io/clay/).

Regular updates are given at the [visual-tools meetings](https://scicloj.github.io/docs/community/groups/visual-tools/).

## Related projects

The [Claykind](https://github.com/timothypratley/claykind) project is rethinking the Clay architecture and implementation from scratch, in a new code base. Currently (Fall 2023), the two projects are being developed in coordination.

[Kindly](https://github.com/scicloj/kindly) is a common ground for defining how things should be visualized, seeking compatibility across tools.

[kindly-advice](https://github.com/scicloj/kindly-advice) is a library that helps tools such as Clay to be Kindly-compatible.

[read-kinds](https://github.com/scicloj/read-kinds) is used internally by Claykind (and soon by Clay as well) to generate Kindly advice from notebooks expressed as Clojure namespaces.

## Discussion

The best places to discuss this project are:
* a topic thread under the [visual-tools stream](https://clojurians.zulipchat.com/#narrow/stream/313390-visual-tools) at the Clojurians Zulip (more about chat streams [here](https://scicloj.github.io/docs/community/chat/)) 
* a [github issue](https://github.com/scicloj/clay/issues)
* a thread at the [visual-tools channel](https://clojurians.slack.com/archives/C02V9TL2G3V) of the Clojurians slack

![quaternary clay in Estonia](https://upload.wikimedia.org/wikipedia/commons/2/2c/Clay-ss-2005.jpg)
(credit: [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Clay-ss-2005.jpg))

## License

Copyright © 2022 Scicloj

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.
