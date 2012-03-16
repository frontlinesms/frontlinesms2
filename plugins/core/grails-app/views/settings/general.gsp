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
				<g:uploadForm name="importForm" controller="import" action="importContacts" method="post">
					<p class="importInfo">To import contacts from version 1, please export them in English</p>
					<label for="importCsvFile">select a data file to import</label>
					<input type="file" id="importCsvFile" name="importCsvFile" onchange="this.form.submit();"/>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
