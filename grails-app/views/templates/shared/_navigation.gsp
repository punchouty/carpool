		<%@ page import="grails.plugin.nimble.core.AdminsService"%>	
		<%@ page import="org.apache.shiro.SecurityUtils"%>		
		
	<div class="navbar  navbar-static-top navbar-bold " role="navigation">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="glyphicon glyphicon-chevron-down"></span>
      </a>
          <a class="navbar-brand" href="${request.contextPath}/"><b>raC looP</b></a>
        </div>
        
       
        <n:notUser>
        <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav navbar-right">        
        
          <g:form controller="auth" action="signin" name="login-form" method="post"
				class="navbar-form pull-left" role="form">				
				<g:if test="${isSignup == null || isSignup == false }">	
				
            <div class="form-group ">
              <input type="text" placeholder="Username" name="username" class="form-control" required  autofocus>
            </div>
            <div class="form-group">
              <input placeholder="Password" class="form-control" type="password" name="password">
            </div>
            <label class="checkbox"> 
                        <input type="hidden" name="_rememberme" /><input type="checkbox" name="rememberme" id="rememberme"  /> Remember Me
            </label>
            <button type="submit" class="btn btn-success">Sign in</button>
            </g:if>
		<g:else>
		<%-- <li><a class="btn" href="${request.contextPath}/">Home</a></li>--%>
		</g:else>
		<input type="hidden" name="targetUri" value="${targetUri}" />				
        </g:form>
		<li class="dropdown pull-right">
              
					<a class=" dropdown-toggle" data-toggle="dropdown" href="#">
						Help <span class="caret"></span>
					</a>
				  <ul class="dropdown-menu">
						<li>
							<a href="${request.contextPath}/faq">FAQ</a>
						</li>
						<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
							<a href="${request.contextPath}/safety">Safety</a>
						</li>
						<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>>
							<a href="${request.contextPath}/etiquettes">Etiquettes</a>
						</li>
						<li>
							<a href="${request.contextPath}/password/forgot">Forgot Password</a>
						</li>
						<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
								controller="staticPage" action="terms">Terms</g:link>
						</li>
					</ul>						
					</li>	
				<g:if test="${ (isSignup == null || isSignup == false) && (isHome == null || isHome == false) }">					
					<li class="pull-right button_space"><a class="" href="${request.contextPath}/signup">Sign Up</a></li>
				</g:if>
				
          </ul>
        </div><!--/.navbar-collapse -->
        </n:notUser>        
        
        
        <g:if test="${SecurityUtils.subject.principal != null}">
         <div class="navbar-collapse collapse">      
          <ul class="nav navbar-nav navbar-right">
           
           <li	<g:if test="${isSearch == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/main">Search</a>
				</li>
				<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/safety">Safety</a>
				</li>
				<li <g:if test="${isActiveJourneys == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/journeys">Active Requests</a>
				</li>
				<li <g:if test="${isHistory == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/history">History</a>
				</li>
				<!-- <li <g:if test="${isHistory == 'true'}">class="active"</g:if>>
					<img src="${currentUser.profile.gravatarUri}" width="42" height="48">
				</li> -->
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">${currentUser.profile.fullName}&nbsp;<i class="glyphicon glyphicon-user"></i>&nbsp; 
					<b class="caret"></b></a>		
						  <ul class="dropdown-menu">
						    <n:hasRole name="${AdminsService.ADMIN_ROLE}">
							<li>
								<g:link controller="admins" action="index" target="_blank">User Administration</g:link>
							</li>
							<li>
								<g:link controller="staticData" action="list">Static Pages</g:link>
							</li>
							</n:hasRole>
							<li>
								<a href="${request.contextPath}/profile">Profile</a>
							</li>
							<li>
								<a href="${request.contextPath}/password/change">Change Password</a>
							</li>
							<li>
								<a href="${request.contextPath}/signout">Sign Out</a>
							</li>
						  </ul>
				</li>		
           
          </ul>
        </div><!--/.nav-collapse -->
        </g:if>
        
      </div>
    </div>