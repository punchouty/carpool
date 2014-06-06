		<%@ page import="grails.plugin.nimble.core.AdminsService"%>
		<%-- 
		<n:isNotLoggedIn>
		<div id='navigation' class="nav-collapse" data-toggle="collapse">
			<g:form controller="auth" action="signin" name="login-form" method="post"
				class="navbar-form form-inline pull-right">
				<g:if test="${isSignup == null || isSignup == false }">	
					<input type="text" name="username" id="navigation.username" class="input-small"
						placeholder="User Id" required  autofocus>
					<input type="password" name="password" id="navigation.password"
						class="input-small" placeholder="Password" required>
					<label class="checkbox"> 
						<g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
					</label>
					<button id="signin_button" type="submit" class="btn">Sign in</button>
				</g:if>
				<g:else>
					<a class="btn" href="${request.contextPath}/">Home</a>
				</g:else>
				<div class="btn-group">
					<a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#">
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
				</div>
				<g:if test="${ (isSignup == null || isSignup == false)required && (isHome == null || isHome == false) }">					
					<a class="btn btn-primary" href="${request.contextPath}/signup">Sign Up</a>
				</g:if>
				<input type="hidden" name="targetUri" value="${targetUri}" />
			</g:form>
			<a href="${request.contextPath}/" class="logo-link"><h1 class="muted">raC looP</h1></a>
		</div>
		</n:isNotLoggedIn>
		<n:isLoggedIn>
		<div id='navigation' class="masthead">
			<ul class="nav nav-pills pull-right">
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
				<li>
					<div class="btn-group">
						<button class="btn">${currentUser.profile.fullName}&nbsp;<i	class="icon-user"></i></button>
						  <button class="btn dropdown-toggle" data-toggle="dropdown">
						    <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu">required
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
							</li>auth/login
						  </ul>
					</div>
				</li>		
			</ul>
			<a href="${request.contextPath}/" class="logo-link"><h1 class="muted">raC looP</h1></a>
		</div>
		</n:isLoggedIn>
		--%>
	<div class="navbar navbar-default navbar-static-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">raC looP</a>
        </div>
        <%--
        <div class="navbar-collapse collapse">
          <g:form controller="auth" action="signin" name="login-form" method="post"
				class="navbar-form navbar-right" role="form">
            <div class="form-group">
              <input type="text" placeholder="Username" name="username" class="form-control" required  autofocus>
            </div>
            <div class="form-group">
              <input placeholder="Password" class="form-control" type="password" name="password">
            </div>
            <button type="submit" class="btn btn-success">Sign in</button>
          </g:form>
        </div><!--/.navbar-collapse -->
         --%>
        
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="#">Action</a></li>
                <li><a href="#">Another action</a></li>
                <li><a href="#">Something else here</a></li>
                <li class="divider"></li>
                <li class="dropdown-header">Nav header</li>
                <li><a href="#">Separated link</a></li>
                <li><a href="#">One more separated link</a></li>
              </ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li><a href="../navbar/">Default</a></li>
            <li class="active"><a href="./">Static top</a></li>
            <li><a href="../navbar-fixed-top/">Fixed top</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>