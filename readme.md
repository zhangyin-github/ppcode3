# ppcode3 Shared Source Project
## Update
* [Expanding Window Code](https://github.com/zhangyin-github/ppcode3/wiki/code-uep-expanding-window) is now available. (2021/12/24)
* [Improved Online Code](https://github.com/zhangyin-github/ppcode3/wiki/code-ep-imrpovedonline) is now available. (2021/12/23)
* [Online Code](https://github.com/zhangyin-github/ppcode3/wiki/code-ep-online) is now available. (2021/12/22)
* We are about to ship our first wave of algorithms. (2021/12/4)
## Introduction
ppcode3 is a simulation toolkit to speed up research on channel coding theory. ppcode3 is expected to:
* be easy to develop
* be easy to use
* provide a bunch of codes to compare their performance
* have acceptable performance

To meet these expectations, ppcode3 is:
* developed with Kotlin to make reading and writing source codes less painful
* highly configurable using config files to enable easy management of experimental setups
* shipped with a bunch of ready-to-use codes and performance measures
* multithreaded to save running time
## Dependencies
ppcode3 use maven to manage all the dependencies. However, [thesallab.configuration](https://github.com/zhangyin-github/thesallab.configuration) has to be installed manually using maven.
## Use
Please visit our [Wiki](https://github.com/zhangyin-github/ppcode3/wiki).
## License
ppcode3 is licensed under the [GNU Lesser General Public License v3.0](https://www.gnu.org/licenses/lgpl-3.0-standalone.html).
## Citation
Please consider cite our research paper
```
@article{zhao2018improved,
  title={Improved online fountain codes},
  author={Zhao, Yuli and Zhang, Yin and Lau, Francis CM and Yu, Hai and Zhu, Zhiliang},
  journal={IET Communications},
  volume={12},
  number={18},
  pages={2297--2304},
  year={2018},
  publisher={IET}
}
```
## Publications
* Zhao, Yuli, et al. "Improved online fountain codes." IET Communications 12.18 (2018): 2297-2304.
* Zhao, Yuli, et al. "Duplicated zigzag decodable fountain codes with the unequal error protection property." Computer Communications 185.1 (2021): 66-78.
