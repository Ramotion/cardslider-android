![header](./header.png)
![Animation](./preview.gif)
# CardSlider for Android
[![Twitter](https://img.shields.io/badge/Twitter-@Ramotion-blue.svg?style=flat)](http://twitter.com/Ramotion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42eb7b00b93645c0812c045ab26cb3b7)](https://www.codacy.com/app/andreylos/cardslider-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Ramotion/cardslider-android&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/Ramotion/cardslider-android/tree/master.svg?style=svg)](https://circleci.com/gh/Ramotion/cardslider-android/tree/master)

## About
This project is maintained by Ramotion, Inc.<br>
We specialize in the designing and coding of custom UI for Mobile Apps and Websites.<br>

**Looking for developers for your project?**<br>
This project is maintained by Ramotion, Inc. We specialize in the designing and coding of custom UI for Mobile Apps and Websites.

<a href="https://ramotion.com/?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android-contact-us/#Get_in_Touch"> 
<img src="https://github.com/ramotion/gliding-collection/raw/master/contact_our_team@2x.png" width="187" height="34"></a> <br>


The [Android mockup](https://store.ramotion.com?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android) available [here](https://store.ramotion.com/product/htc-one-a9-mockups?utm_source=gthb&utm_medium=special&utm_campaign=cardslider-android).

## Requirements
​
- Android 4.4 KitKat (API lvl 19) or greater
- Your favorite IDE

## Installation
​
Just download the package from [here](http://central.maven.org/maven2/com/ramotion/cardslider/card-slider/0.1.0/card-slider-0.2.0.aar) and add it to your project classpath, or just use the maven repo:

Gradle:
```groovy
'com.ramotion.cardslider:card-slider:0.2.0'
```
SBT:
```scala
libraryDependencies += "com.ramotion.cardslider" % "card-slider" % "0.2.0"
```
Maven:
```xml
<dependency>
	<groupId>com.ramotion.cardslider</groupId>
	<artifactId>card-slider</artifactId>
	<version>0.2.0</version>
</dependency>
```
​

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

# Get the Showroom App for iOS to give it a try
Try our UI components in our iOS app. Contact us if interested.

<a href="https://itunes.apple.com/app/apple-store/id1182360240?pt=550053&ct=cardslider-android&mt=8" > 
<img src="https://github.com/ramotion/gliding-collection/raw/master/app_store@2x.png" width="117" height="34"></a>
<a href="https://ramotion.com/?utm_source=gthb&utm_medium=special&utm_campaign=card-sliderandroid-contact-us/#Get_in_Touch"> 
<img src="https://github.com/ramotion/gliding-collection/raw/master/contact_our_team@2x.png" width="187" height="34"></a>
<br>
<br>

Follow us for the latest updates 
<br>
[![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=https://github.com/ramotion/cardslider-android)
[![Twitter Follow](https://img.shields.io/twitter/follow/ramotion.svg?style=social)](https://twitter.com/ramotion)
