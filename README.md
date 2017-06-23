[![header](./header.png)](https://business.ramotion.com?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android-logo)

# CardSlider for Android
[![Twitter](https://img.shields.io/badge/Twitter-@Ramotion-blue.svg?style=flat)](http://twitter.com/Ramotion)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42eb7b00b93645c0812c045ab26cb3b7)](https://www.codacy.com/app/andreylos/cardslider-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Ramotion/cardslider-android&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/Ramotion/cardslider-android/tree/master.svg?style=svg)](https://circleci.com/gh/Ramotion/cardslider-android/tree/master)

## About
This project is maintained by Ramotion, Inc.<br>
We specialize in the designing and coding of custom UI for Mobile Apps and Websites.<br><br>**Looking for developers for your project?** 

<a href="https://business.ramotion.com?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android-contact-us/#Get_in_Touch" > <img src="https://github.com/Ramotion/navigation-stack/raw/master/contact_our_team@2x.png" width="150" height="30"></a>

![Animation](./preview.gif)

The [Android mockup](https://store.ramotion.com?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android) available [here](https://store.ramotion.com/product/htc-one-a9-mockups?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android).

## Requirements
​
- Android 4.4 KitKat (API lvl 19) or greater
- Your favorite IDE

## Installation
​
...

## Basic usage

`CardSlider` is a custom `LayoutManager` for `RecyclerView`.
You can attach it to RecyclerView from code or XML layout.

Here are the attributes you can specify in the constructor or XML layout:
* `activeCardLeft` - Active card offset from start of RecyclerView. Default value is 50dp.
* `cardWidth` - Card width. Default value is 148dp.
* `cardsGap` - Distance between cards. Default value is 12dp.

For card snapping, there is `CardSnapHelper` class.'


```
...
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new CardSliderLayoutManager(this););

    new CardSnapHelper().attachToRecyclerView(recyclerView);
    ...
}
```

You can find this and other, more complex, examples in this repository ​

## License
​
CardSlider for Android is released under the MIT license.
See [LICENSE](./LICENSE.md) for details.

## Follow us

[![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=https://github.com/ramotion/cardslider-android)
[![Twitter Follow](https://img.shields.io/twitter/follow/ramotion.svg?style=social)](https://twitter.com/ramotion)
