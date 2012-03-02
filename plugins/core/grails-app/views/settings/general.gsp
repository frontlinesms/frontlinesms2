<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > General</title>
	</head>
	<body>
		<div id='general'>
			<div id="import">
				<table>
					<tr class="generalsec">
						<td class="entry-content">Import&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td class="entry-date">Import data from a previous backup</td>
					</tr>
					<tr class="generalsec">
						<td class="entry-content"></td>
						<td class="entry-date">
							<g:uploadForm name="importform" controller="import" action="importedContacts" method="post" >
							<div class="importinfo">To import contacts from version 1, please export them in English</div>
							<input type="file" id="importedcsvfile" name="importedcsvfile" onchange="this.form.submit();"> 
							<span class="fileimportcaption">select a data file to import</span>

							</g:uploadForm>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>
