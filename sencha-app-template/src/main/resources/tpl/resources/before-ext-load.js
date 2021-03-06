/* from tpl */
var joo = joo || {};
joo.localization = joo.localization || {};

joo.localeSupport = (function() {
  var DAYS_TILL_LOCALE_COOKIE_EXPIRY = 10*356;

  function escape(s/*:String*/)/*:String*/ {
    return s.replace(/([.*+?^${}()|[\]\/\\])/g, "\\$1");
  }

  function getCookie(name/*:String*/)/*:String*/ {
    var cookieKey/*:String*/ = escape(name);
    var match/*:Array*/ = document.cookie.match("(?:^|;)\\s*" + cookieKey + "=([^;]*)");
    return match ? decodeURIComponent(match[1]) : null;
  }

  function getLocaleCookieExpiry()/*:Date*/ {
    var date/*:Date*/ = new Date();
    date.setTime(date.getTime() + (DAYS_TILL_LOCALE_COOKIE_EXPIRY * 24 * 60 * 60 * 1000));
    return date;
  }

  function setCookie(name, value, path, expires, domain) {
    //noinspection FallThroughInSwitchStatementJS
    document.cookie =
            name + "=" + encodeURIComponent(value || "") +
            ((expires === null) ? "" : ("; expires=" + expires.toGMTString())) +
            ((path === null) ? "" : ("; path=" + path)) +
            ((domain === null) ? "" : ("; domain=" + domain))
  }

  return {
    preferredLocales: joo.localization.preferredLocales || [],
    supportedLocales: joo.localization.supportedLocales || ["en"],
    localeCookieName: joo.localization.localeCookieName || "joo.locale",
    localeCookiePath: joo.localization.localeCookiePath || location.pathname,
    localeCookieDomain: joo.localization.localeCookieDomain || null,

    getSupportedLocales: function () {
      return this.supportedLocales.concat(); // return a copy
    },

    getDefaultLocale: function () {
      return this.supportedLocales[0];
    },
    readLocaleFromCookie: function ()/*:String*/ {
      return this.findSupportedLocale(getCookie(this.localeCookieName));
    },

    setLocale: function(newLocale/*:String*/)/*:String*/ {
      this.locale = this.findSupportedLocale(newLocale);
      // either create, update or remove (if locale===null) the Cookie:
      setCookie(this.localeCookieName, this.locale, this.localeCookiePath,
              this.locale ? getLocaleCookieExpiry() : new Date(0),
              this.localeCookieDomain);
      return this.getLocale(); // use getter to re-compute fallback logic for locale==null and cache the result
    },

    findSupportedLocale: function(locale/*:String*/)/*:String*/ {
      if (!locale) {
        return null;
      }
      // find longest match of locale in supported locales
      var longestMatch/*:String*/ = "";
      for (var i/*:int*/ = 0; i < this.supportedLocales.length; i++) {
        if (locale.indexOf(this.supportedLocales[i]) === 0
                && this.supportedLocales[i].length > longestMatch.length) {
          longestMatch = this.supportedLocales[i];
        }
      }
      return longestMatch ? longestMatch : null;
    },

    getLocale: function()/*:String*/ {
      if (!this.locale) {
        this.locale = this.readLocaleFromCookie() || this.getLocaleFromPreferredLocales() || this.readLocaleFromNavigator() || this.getDefaultLocale();
      }
      return this.locale;
    },


    //privates
    getLocaleFromPreferredLocales: function() {
      for (var i/*:int*/ = 0; i < this.preferredLocales.length; i++) {
        var preferredLocale/*:String*/ = this.findSupportedLocale(this.preferredLocales[i]);
        if (preferredLocale) {
          return preferredLocale;
        }
      }
      return null;
    },
    readLocaleFromNavigator: function()/*:String*/ {
      if (navigator) {
        var locale/*:String*/ = navigator['language'] || navigator['browserLanguage']
                || navigator['systemLanguage'] || navigator['userLanguage'];
        if (locale) {
          return this.findSupportedLocale(locale.replace(/-/g, "_"));
        }
      }
      return null;
    }
  }
})();

var Ext = Ext || {};

// Tell Ext which locale to use
Ext.beforeLoad = function (tags) {
  var locale = joo.localeSupport.getLocale();

  // setup app manifest
  // only set manifest if index.html doesn't already contain a manifest
  if (!document.head.querySelector("link[rel='manifest']")) {
    var appManifestFile = "app-manifest" + (locale !== "en" ? "-" + locale : "") + ".json";
    /**
     * @type {HTMLLinkElement}
     */
    var appManifestLink = document.createElement("link");
    appManifestLink.rel = "manifest";
    appManifestLink.href = appManifestFile;
    document.head.appendChild(appManifestLink)
  }

  Ext.manifest = locale; // this name must match a json file name generated by some build

  Ext.registerGlobalResources = function(idToPackageResourceUrl) {
    for (var id in idToPackageResourceUrl) {
      var packageResourceUrl = idToPackageResourceUrl[id];
      var resolvedPath = Ext.resolveResource(packageResourceUrl);
      if (resolvedPath.indexOf('/') !== 0) {
        var pathname = window.location.pathname;
        resolvedPath = pathname.substring(0, pathname.lastIndexOf('/') + 1) + resolvedPath;
      }
      Ext.manifest.globalResources[id] = resolvedPath;
    }
  };

  /*
    Patch the expensive Ext.Boot#canonicalUrl which is called multiple times for a given url.
    Store computed canonical urls in Ext.Boot.urls.
  */
  Ext.Boot.urls = {};
  var oldCanonicalUrl = Ext.Boot.canonicalUrl;
  Ext.Boot.canonicalUrl = function(url) {
    var cachedUrl = Ext.Boot.urls[url];
    if (cachedUrl) {
      return cachedUrl;
    }
    var ret = oldCanonicalUrl(url);
    Ext.Boot.urls[url] = ret;
    return ret;
  };

  // This function is called once the manifest is available but before
  // any data is pulled from it.
  return function (manifest) {
    // peek at / modify the manifest object
    manifest.locale = locale;
  };
};
