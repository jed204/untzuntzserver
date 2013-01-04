if (!Core.get(window, ["UStackServerAPI"])) {
    Core.set(window, ["UStackServerAPI"], {});
}

UStackServerAPI = Core.extend({

	appKey: null,
	host: null,
	secure: null,
	clientId: null,
	apiKey: null,
	responseArea: null,

    $static: {
    
	    init: function() {
		
	    	Core.Debug.consoleWrite("UStackServer API Initialized");
		
	    }
	    
    },
    
    $load: function() {
    
    	Core.Debug.consoleWrite("UStackServer API Loaded");

    },
    
    $construct: function() {
    
    	Core.Debug.consoleWrite("UStackServer API Constructed");

    },

    setAppKey: function(key) {
    	this.appKey = key;
    },

    setHost : function(host) {
    	this.host = host;
    },
    
	setSecure : function(sec) {
		this.secure = sec;
	},
	
	setReponseArea : function(ra) {
		this.responseArea = ra;
	},

	parameterize : function(params) {
	  var base  = "";
	  var tail = [];
	  for (var p in params) {
	    if (params.hasOwnProperty(p)) {
	    	var val = escape(params[p]);
	    	if (val.length > 0)
	    		tail.push(p + "=" + val);
	    }
	  }
	  
	  if (this.userName != null)
		  tail.push("username=" + escape(this.userName));
	  if (this.password != null)
		  tail.push("accesscode=" + escape(this.password));
	  
	  return base + tail.join("&")
	},
	
	setPassword : function(pass) {
		this.password = pass;
	},
	
	setUserName : function(user) {
		this.userName = user;
	},

	call: function(method, call, requestParams) {
	
		var fullRequest = 'http://';
		if (this.secure)
		{
			fullRequest = 'https://';
		}
		
		fullRequest += this.host;
		fullRequest += '/api/';
		fullRequest += call;

    	Core.Debug.consoleWrite("API Call [ " + method + " " + fullRequest + " ]");

    	var conn = null;
		if (method == 'GET')
		{
			var paramQS = this.parameterize(requestParams);
	    	Core.Debug.consoleWrite("API Parameters [ " + paramQS + " ]");
	    	fullRequest += '?' + paramQS;
	    	conn = new Core.Web.HttpConnection(fullRequest, method, null, 'text/plain');
		}
		
    	if (conn != null)
    	{
    		conn.addResponseListener(Core.method(this, this._retrieveListener));
            conn.connect();
        }
	},

	_syntaxHighlight : function(json) {
	    if (typeof json != 'string') {
	         json = JSON.stringify(json, undefined, 2);
	    }
	    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
	        var cls = 'number';
	        if (/^"/.test(match)) {
	            if (/:$/.test(match)) {
	                cls = 'key';
	            } else {
	                cls = 'string';
	            }
	        } else if (/true|false/.test(match)) {
	            cls = 'boolean';
	        } else if (/null/.test(match)) {
	            cls = 'null';
	        }
	        return '<span class="' + cls + '">' + match + '</span>';
	    });
	},

	_prettyPrint : function(e)
	{
		try {
			var jsObject = JSON.parse(e);
			return this._syntaxHighlight(JSON.stringify(jsObject, undefined, 4));
		} catch (er) {
			Core.Debug.consoleWrite("Failed to parse/print : " + er);
			return e;
		}
	},
	
	_responseOutput : function(e)
	{
		if (this.responseArea)
			this.responseArea.innerHTML = "<pre class='ustackResponse'>" + this._prettyPrint( e ) + "</pre>";
	},

	_retrieveListener : function(e)
	{
		if (!e.valid)
		{
			Core.Debug.consoleWrite("Invalid HTTP response retrieving library \"" + e.source._url + "\", received status: " + e.source.getStatus());
			Core.Debug.consoleWrite("API Response:\n" +  e.source.getResponseText() + "\n");
			this._responseOutput(e.source.getResponseText());
			return;
		}

		if (e.source.getStatus() == 400)
		{
			if (this.responseArea) {
				this.responseArea.innerHTML = "<pre class='ustackResponse'>API Error Returned</pre>";
			}
		
			return;
		}
			
	    Core.Debug.consoleWrite("API Response:\n" + e.source.getResponseText() + "\n");
		this._responseOutput(e.source.getResponseText());
	}

});
