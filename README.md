<h1>Clocked Tool</h1>
<p>A tool for people who can't view the punch system</p>

<ul>
<li>Click the dial to establish in/out punches</li>
<li>Click the triangular button in the bottom right corner to re-align the application</li>
<li>Click the triangular button with three dots in the top left corner to view your punch history</li>
<li>Click the "Free Spin!" checkbox to enable the dial to spin freely without logging punches</li>
</ul>

<h2>History</h2>

<ul>
<li>Click the back button to return to the main screen</li>
<li>Click the left and right arrows to navigate between punch times</li>
<li>Click the "Verbose" checkbox to view the time between in/out punch times</li>
<li>Click on punches to delete them (click on the 'X' to confirm)</li>
</ul>

<h2>Features</h2>
<h3>1.0</h3>
<ul>
<li>"One Week Ago" - Provides a clock time (in red) that you should aim to clock out by to achieve the same number of hours as the same day in the prior week
  <ul>
  <li>The time will turn green when you have met or exceeded the time listed</li>
  <li>The time will <b>not</b> show up if you didn't work the same day in the previous week</li>
  </ul>
</li>
</ul>

<h3>1.0.1</h3>
<ul>
<li>"Full Time" - Overrides "One Week Ago" with a clock time (in red) that you should aim to clock out by to achieve 8 hours
  <ul>
  <li>The time will turn green when you have met or exceeded the time listed</li>
  <li>The time <b>will</b> show up if and if you didn't work the same day in the previous week</li>
  </ul>
</li>
</ul>

<h3>1.0.2</h3>
<ul>
<li>Fixed bug: Clocked Tool started clocked in, not clocked out
  <ul>
  <li>Fixed by changing the orientation of the dial when you start it</li>
  <li>Add a time to history.log (in the folder containing the .jar file) to offset if the dial is offset</li>
  </ul>
</li>
</ul>

<h3>1.0.3</h3>
<ul>
<li>Fixed bug: Clocked tool history could get offset from dial if edited improperly or the app was of a version prior to 1.0.2
  <ul>
  <li>Added a "Free Spin!" checkbox which allows you to change the dial state without logging punches</li>
  </ul>
</li>
<li>Punch Delete - You can now delete log entries from the history page instead of going into history.log
  <ul>
  <li>Click on a punch time in the history page to delete</li>
  <li>Click on the 'X' to confirm</li>
  <li><b>You will be moved to the current date or a blank page if you delete a log page you are currently on</b></li>
  </ul>
</li>
</ul>

<h3>1.0.4</h3>
<ul>
<li>Fixed bug: CPU was constantly being used due to unnecessary graphics refreshes
  <ul>
  <li>Graphics updates are driven by click events only</li>
  <li>Animations (like the Dial) start an animation timer and stop it when the animation finishes</li>
  </ul>
</li>
<li>Fixed bug: difference between punches in Verbose mode turned black upon toggling deletion of punch</li>
<li>Made "Free Spin!" checkbox a bit bigger on the main page
  <ul>
  <li>Removed "Free" part and increased the checkbox size</li>
  </ul>
</li>
</ul>

<h3>1.0.5</h3>
<ul>
<li>Fixed bug: history page wasn't refreshing information
  <ul>
  <li>History page now has an update timer that refreshes the information every 30 seconds</li>
  <li>Data is still refreshed every time you enter the history page or spin the dial</li>
  </ul>
</li>
</ul>

<h3>1.0.6</h3>
<ul>
<li>Week Hours - You can now see how many hours you've worked in the week on the History page
  <ul>
  <li>The History page now has a checkbox which enables viewing of the hours you've worked in the week</li>
  <li>The week <b>starts</b> on Sunday and <b>ends</b> on Saturday</li>
  </ul>
</li>
</ul>

<h3>1.0.7</h3>
<ul>
<li>Quicker history navigation - By pressing the Control key, you can navigate through your punches twice as fast</li>
</ul>

<h3>1.0.8</h3>
<ul>
<li>The history corner button is now labeled with an 'H' and the new settings panel
is labelled with an 'S'
<li>Palette Editor - You can now change the colors of the in and out hemicircles 
  <ul>
  <li>Using the 'color picker' on the new settings panel, you can set the colors behind "IN" and "OUT" with just a couple clicks</li>
  <li><b>NOTE:</b> The colors selected are not persisted when the application is closed.</li>
  </ul>
</li>
</ul>
