<!-- 
    -- READ ME -- 
    -------------
    
	This is the original console.jsp index file.
    
    While developing is it possible to use this file, 
    but any modification MUST be manually copied into console.jsp
    after minification of HTML.
    
    It has been minified in to improve page loading.
-->
<!doctype html>
<html>
    <head>
        <!-- 
            Meta tag informations
         -->
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=9">
        
        <!-- Manage the localization of the console -->
        <% if (request.getParameter("l") != null) {  %>
        	<meta name="gwt:property" content="locale=<%=request.getParameter("l")%>">
        <% } else if (request.getHeader("Accept-Language") != null) { %>
	        <meta name="gwt:property" content="locale=<%=request.getHeader("Accept-Language").split(",")[0]%>"> 
        <% } else { %>
	        <meta name="gwt:property" content="locale=en">
	    <% } %>
        
        <!-- 
            Favicon and title definition
         -->
        <title>Kapua&copy; Console</title>
        
        <!-- 
            CSS resources
         -->
        <link rel="stylesheet" type="text/css" href="gxt/css/gxt-all.css" id="gxtCss">
        <link rel="stylesheet" type="text/css" id="gray" href="gxt/css/gxt-gray.css">
        
        <!-- 
            JS resources
         -->
        <script type="text/javascript" src="js/jquery/jquery-1.4.2.js"></script>
        <script type="text/javascript" src="console/console.nocache.js"></script>
        
        <!-- This script manage all JS/CSS deferred loading -->
        <script type="text/javascript" src="js/kapuaconsole/console.js" defer></script>

    </head>
    <body>
    </body>
</html>