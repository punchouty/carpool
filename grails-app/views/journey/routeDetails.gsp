<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="dynamic" />
<title>Route Details</title>
<r:require module="core" />
</head>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	<section class="white-bg" id="section10">
        <div class="container-fluid">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                
                <h2 class="dark-text"><strong>Route</strong> Details</h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
	<div class="container-fluid">
	 	<g:render template="/templates/misc/journeyDetails" model="${[id: id, wayPoints:wayPoints, priceMap:priceMap]}" /> 
     </div>
     <g:link action="backToSearchResult" id="backToSearchResult">                        	
      	<button class="btn btn-danger"><i class="fa fa-mail-reply"></i> Go Back</button>
     </g:link> 
</body>

</html>