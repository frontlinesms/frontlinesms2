<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title>Settings > General</title>
		<export:resource />
	</head>
	<body>
		<div id="general">
			<div id="import">
				<h2>Import</h2>
				<p class="description">
					Import data from a previous backup
				</p>
				<g:uploadForm name="importForm" controller="import" action="importData" method="post">
					<div>
						<h3>Select type of data to import:</h3>
						<input type="radio" name="data" value="contacts" checked="checked" /> Contact details<br />
						<input type="radio" name="data" value="messages" /> Message details
					</div>
					<p class="importInfo">To import data from version 1, please export them in English</p>
					<label for="importCsvFile">select a data file to import</label>
					<input type="file" id="importCsvFile" name="importCsvFile" onchange="this.form.submit();"/>
				</g:uploadForm>
				<h3>Export failed contacts below</h3>
				<export:formats formats="['csv']" controller="import" action="exportFailedContacts" />
			</div>
		</div>
	</body>
</html>
