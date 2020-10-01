# VaaaS

This repository contains _VaaaS_ which stands for Vickrey-auction-as-a-Service.

_VaaaS_ is a small side project intended as a showcase in:

- Modern _pure_ functional Scala using **ZIO, cats**
- Event-sourced algebras using **aecor**, [the pure functional event-sourcing runtime](https://github.com/notxcain/aecor)
- Domain-driven design: bounded contexts and write/read model separation

The accompanying post can be found here [here](https://pucilowski.com/posts/vaaas/).

# Background

This project was inspired by a [post](https://kevinlynagh.com/notes/pricing-niche-products/) by Kevin Lynagh
in which he advises his friend to trial out the Vickrey auction as a price discovery mechanism for his niche goods.

Very briefly:
>A Vickrey auction is a type of sealed-bid auction. Bidders submit written bids without knowing the bid of the other people in the auction. The highest bidder wins but the price paid is the second-highest bid.

The above can be generalised to _N_ items where top _N_ bidders get the item but pay the _N+1_'th bid.
