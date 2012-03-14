<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title>Settings > General</title>
	</head>
	<body>
		<div id="general">
			<div id="import">
				<h2>Import</h2>
				<p class="description">
					Import data from a previous backup
				</p>
				<g:uploadForm name="importform" controller="import" action="importedContacts" method="post">
					<p class="importinfo">To import contacts from version 1, please export them in English</p>
					<label for="importedcsvfile">select a data file to import</label>
					<input type="file" id="importedcsvfile" name="importedcsvfile" onchange="this.form.submit();"/>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
