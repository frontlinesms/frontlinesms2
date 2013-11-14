<p class="api-url">${c.getFullApiUrl(request)} | 
<em class="smssync-lastconnected">${g.message(code:"smssync.lastConnected.${c.lastConnectionTime ? 'time' : 'never'}", args:[c.lastConnectionTime ? g.formatDate(date:c.lastConnectionTime):null])}</em>
