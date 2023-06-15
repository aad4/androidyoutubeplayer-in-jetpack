# Example Jetpack Compose app with androidyoutubeplayer library

This small project shows a simple integration of [the YouTube library of Pierfrancesco Soffritti](https://github.com/PierfrancescoSoffritti/android-youtube-player) in Jetpack Compose, and the problems I have getting it to work properly.

I try to use the library for the following use cases:
1. Multiple video players can be on a single screen, adhering to the spacing between videos that I provide
1. When not all videos fit on the screen, vertical scrolling is used to scroll through all videos of the page
1. The app allows for rotating the screen
1. A video player allows for switching to full screen and back again
1. When switching to full screen, the sound/video continues to play (almost) without interruption. Same when switching back to normal view.

As far as I understand, the library internally uses a WebView that uses the YouTube IFrame API. His code examples contain a full screen example, but that uses the old Android XML approach with 2 views, one for the normal (small) player and one for the full screen player. In Jetpack Compose there are no XML views, but there are possibilities to combine the two technologies.

# Simplest approach (no fullscreen)

The first thing I tried is to include a single YouTube player in a Jetpack Compose project (see commit [Simple version without fullscreen](https://github.com/aad4/androidyoutubeplayer-in-jetpack/commit/780ffc3c1bdd23c2b45dd0a8d449f67e76112a98)).

The player loads, and when clicking on the video it starts playing properly.

But, I see following issue:
- When I rotate the screen (landscape), the youtube player window does not fully display: the controls at the bottom of the player window are not visible and I cannot scroll down to make them visible.

# Extended example (multiple videos & full screen)

Then I extended the example to render multiple videos on a page, and to allow for switching to full screen and back again (see commit [With full screen code (that does not work) ](https://github.com/aad4/androidyoutubeplayer-in-jetpack/commit/172a77d1bdf2e0a66210586afd1b4ad0f7bc5370)).

For the second view that the library uses for full screen rendering I added a LinearLayout with a FrameLayout to the res folder. Inside the AndroidView factory, I inflate the view and use it in the callbacks for going to full screen and back again.

Again, the players load, and when clicking a player it will start playing (and it will stop the other player when that was playing). So far, so good.

But, I see the following issues:
- When rotating the screen to landscape the distance between the two videos is zero, whereas in portrait the distance between the two videos is the specified 32.dp.
- Also, in landscape mode both videos are shown partly (bottom part of the first video and top part of the second video).
- Also, in landscape mode vertical scroll is not working
- When clicking the full-screen button (in protrait mode), the small player disappears (which is expected), but the full screen player does not appear. Sound also stops (note: only the first video has sound).

# Questions

1. Are the use cases mentioned above even possible with the library?
1. How to get full screen mode working in Jetpack Compose?
1. Why is the landscape mode resulting in strange layout/behavior (bottom part of the video of the first commit not visible, scrollability for both commits, adhering to spacing between videos for second commit)?