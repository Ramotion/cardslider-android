<a href="https://www.ramotion.com/agency/app-development?utm_source=gthb&utm_medium=repo&utm_campaign=cardslider-android"><img src="https://github.com/Ramotion/folding-cell/blob/master/header.png"></a>

<a href="https://github.com/Ramotion/cardslider-android">
<img align="left" src="https://github.com/Ramotion/cardslider-android/blob/master/preview.gif" width="480" height="360" /></a>

<p><h1 align="left">CARDSLIDER [JAVA]</h1></p>

<h4>Material design UI controller that allows to swipe through cards with pictures and descriptions</h4>


___


<p><h6>We specialize in the designing and coding of custom UI for Mobile Apps and Websites.</h6>
<a href="https://www.ramotion.com/agency/app-development?utm_source=gthb&utm_medium=repo&utm_campaign=cardslider-android">
<img src="https://github.com/ramotion/gliding-collection/raw/master/contact_our_team@2x.png" width="187" height="34"></a>
</p>
<p><h6>Stay tuned for the latest updates:</h6>
<a href="https://goo.gl/rPFpid" >
<img src="https://i.imgur.com/ziSqeSo.png/" width="156" height="28"></a></p>

</br>

[![Twitter](https://img.shields.io/badge/Twitter-@Ramotion-blue.svg?style=flat)](http://twitter.com/Ramotion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42eb7b00b93645c0812c045ab26cb3b7)](https://www.codacy.com/app/andreylos/cardslider-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Ramotion/cardslider-android&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/Ramotion/cardslider-android/tree/master.svg?style=svg)](https://circleci.com/gh/Ramotion/cardslider-android/tree/master)
[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://paypal.me/Ramotion)

## Requirements
​
- Android 4.4 KitKat (API lvl 19) or greater
- Your favorite IDE

## Installation
​
Just download the package from [here](http://central.maven.org/maven2/com/ramotion/cardslider/card-slider/0.3.1/card-slider-0.3.1.aar) and add it to your project classpath, or just use the maven repo:

Gradle:
```groovy
'com.ramotion.cardslider:card-slider:0.3.1'
```
SBT:
```scala
libraryDependencies += "com.ramotion.cardslider" % "card-slider" % "0.3.1"
```
Maven:
```xml
<dependency>
	<groupId>com.ramotion.cardslider</groupId>
	<artifactId>card-slider</artifactId>
	<version>0.3.1</version>
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

## 📄 License

Cardslider Android is released under the MIT license.
See [LICENSE](./LICENSE) for details.

This library is a part of a <a href="https://github.com/Ramotion/android-ui-animation-components-and-libraries"><b>selection of our best UI open-source projects</b></a>

If you use the open-source library in your project, please make sure to credit and backlink to www.ramotion.com

## 📱 Get the Showroom App for Android to give it a try
Try this UI component and more like this in our Android app. Contact us if interested.

<a href="https://play.google.com/store/apps/details?id=com.ramotion.showroom" >
<img src="https://raw.githubusercontent.com/Ramotion/react-native-circle-menu/master/google_play@2x.png" width="104" height="34"></a>

<a href="https://www.ramotion.com/agency/app-development?utm_source=gthb&utm_medium=repo&utm_campaign=cardslider-android">
<img src="https://github.com/ramotion/gliding-collection/raw/master/contact_our_team@2x.png" width="187" height="34"></a>
