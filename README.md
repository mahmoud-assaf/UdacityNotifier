## This app is not official app of Udacity by any means 

# UdacityNotifier
 App for notifying Udacity reviewers about new assigned questions 

Quick tool I've developed that helped me monitor my Reviewer dashboard for any new assigned questions (before Udacity implemented email notification of new assignments) 
so i published it to help any fellow reviewer who might missed questions (as questions are time limited before reassigned) due to delayed email client synchronization may be. 

**Requirements**

-Network connection

-Draw over other apps permission 

**How it works**

Simply the foreground service opens dashboard page inside webview(0X0 dimensions) and search for word "Answer" if it's found then notify user about new question.. 

**Is it optimized or there is other better way**

-May be i don't know.. i just needed quick implementation that came in my mind, you can try with OkHttp or other may be. 

**How to use**

-[Download](https://raw.githubusercontent.com/mahmoud-assaf/UdacityNotifier/master/UdacityNotifier-release.apk) and Open the attached app. 

-Grant Draw over other apps (to display the invisible webview window)

-Login in to dashboard (Reviews page), and make sure your dashboard loaded successfully
 
-Activate Service. 

-Service will be run as foreground service to survive android kill. 

-The service open the dashboard every 5 minutes and search for key word (in this case "Answer") and if found., notify user. 

-You may deactivate service temporarily when assigned new question until you finish answering it. 

