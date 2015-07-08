  1. Put **Latest Release** and **Archived Release** in SVN ,use eclipse site.(Always back up Latest Release to Archived Release)
  1. Put **Integration Builds** in _Downloads_,use zipped file.(Don't back up,to treasure SVN disk space)

Grateful if you know a more automatic releaseing style.

# Build the ${site folder} in local disk #

  * [BuildingTheCode](BuildingTheCode.md)
  * Get projects for release:
    1. Feature Project:https://camel-route-viewer.googlecode.com/svn/updates/project4release/com.googlecode.camelrouteviewer.feature
    1. Updates Project:https://camel-route-viewer.googlecode.com/svn/updates/project4release/com.googlecode.camelrouteviewer.updates

  * Update version number:Edit version number in **"Plugin Project">>plugin.xml** and **"Feature Project">>feature.xml**
> > for example,from  **1.1.0\_v20080830** to **1.2.0\_v20080930**


  * Update feature in the site.xml of "Updates Project",always remove the older feature,then add the newer feature just had increated version number. Because I found that **Synchronize** button didn't work when version number changed.



  * Press **Build** button in **Updates Project>>site.xml>>Site Map**,then in **Updates Project** will generate two folders:features and plugins.

  * Yeah,The **Updates Project** folder is just the **${site folder}**.Be care to remove older generated features jar if exists.

# Commit the update site to Release #

  * Copy the **latest\_release** to **archived\_releases** using the following command,for example:


> `svn copy https://camel-route-viewer.googlecode.com/svn/updates/latest_release     https://camel-route-viewer.googlecode.com/svn/updates/archived_releases/1.0.0 -m "archive 1.0.0 release"`

  * subversion commit **${site folder}**  to https://camel-route-viewer.googlecode.com/svn/updates/latest_release

# Update Documents #

  * Update Site and Wiki documents,Release Notes、ChangeLog、 etc.


# Reference pictures #
![http://camel-route-viewer.googlecode.com/svn/wiki/pictures/release_plugin_process/projects4release.gif](http://camel-route-viewer.googlecode.com/svn/wiki/pictures/release_plugin_process/projects4release.gif)
![http://camel-route-viewer.googlecode.com/svn/wiki/pictures/release_plugin_process/managing_site.gif](http://camel-route-viewer.googlecode.com/svn/wiki/pictures/release_plugin_process/managing_site.gif)