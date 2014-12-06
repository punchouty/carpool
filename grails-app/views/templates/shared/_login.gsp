<%@ page import="grails.plugin.nimble.core.AdminsService"%>	
		<%@ page import="org.apache.shiro.SecurityUtils"%>	
<%--		<div class="masthead">
			<ul class="nav nav-pills pull-right">
				<n:isNotLoggedIn>
					<li	<g:if test="${isHome == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}">Home</a>
					</li>
					<li	<g:if test="${isFaq == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}/faq">FAQ</a>
					</li>
					<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}/safety">Safety</a>
					</li>
					<g:if test="${isSignin == null || isSignin == 'false'}">
					<li>
						<a href="${request.contextPath}/signin">Sign In</a>
					</li>
					</g:if>
					<g:if test="${isSignup == null || isSignup == 'false'}">
					<li class="active">
						<a href="${request.contextPath}/signup">Sign Up</a>
					</li>
					</g:if>
				</n:isNotLoggedIn>
				<n:isLoggedIn>
					<li	<g:if test="${isHome == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}">Home</a>
					</li>
					<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}/safety">Safety</a>
					</li>
					<li	<g:if test="${isFaq == 'true'}">class="active"</g:if>>
						<a href="${request.contextPath}/faq">FAQ</a>
					</li>
					 
					<li class="active dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"> Home <b class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="terms">Terms</g:link></li>
							<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="etiquettes">Etiquettes</g:link></li>
							<li <g:if test="${isAbout == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="about">About</g:link></li>
						</ul></li>
					
					<li>
						<a href="${request.contextPath}/requests">Active Requests</a>
					</li>
					<li>
						<a href="${request.contextPath}/notifications">Notifications <span class="badge badge-important">4</span></a>
					</li>
					<li>
						<a href="${request.contextPath}/history">History</a>
					</li>
					<li class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#"> 
							${currentUser.profile.fullName}&nbsp;<i	class="icon-user"></i> <b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							<n:hasRole name="${AdminsService.ADMIN_ROLE}">
							<li>
								<g:link controller="admins" action="index">Admin Functions</g:link>
							</li>
							</n:hasRole>
							<li>
								<a href="${request.contextPath}/profile">Profile</a>
							</li>
							<li>
								<a href="${request.contextPath}/password/change">Change Password</a>
							</li>
							<li>
								<a href="${request.contextPath}/signout">Profile</a>
							</li>
						</ul>
					</li>
				</n:isLoggedIn>
			</ul>
			<a href="${request.contextPath}" class="logo-link"><h1 class="muted">raC looP</h1></a>
		</div>--%>
		
		
	
		
		
		
	<!-- STICKY NAVIGATION -->
            <div class="navbar navbar-inverse bs-docs-nav navbar-fixed-top sticky-navigation" role="navigation">
                <div class="container">
                    <div class="navbar-header">

                        <!-- LOGO ON STICKY NAV BAR -->
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#stamp-navigation">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-grid-2x2"></span>
                        </button>

                        <!-- LOGO -->
                        <a class="navbar-brand logo-racloop" href="${request.contextPath}">
                            RACLOOP
                            <!-- <img src="images/logo-nav.png" alt=""> -->
                        </a>

                    </div>

                    <!-- TOP BAR -->
                    <div class="navbar-collapse collapse" id="stamp-navigation">

                        <!-- NAVIGATION LINK -->
                        <ul class="nav navbar-nav navbar-left main-navigation small-text">
                            <li><a href="${request.contextPath}/etiquettes">Etiquettes</a>
                            </li>
                            <li><a href="${request.contextPath}/faq">FAQ</a>
                            </li>
                            <li><a href="${request.contextPath}/about">About</a>
                            </li>
                        </ul>

                        <!-- LOGIN REGISTER -->
                        <ul class="nav navbar-nav navbar-right login-register small-text">
                            <n:isLoggedIn>
                            <li class="login">
                                <a href="${request.contextPath}/search"><i class="icon-basic-magnifier"></i> Search</a>
                            </li>
                            <li class="login">
                                <a href="${request.contextPath}/journeys"><i class="icon-basic-bookmark"></i> My Requests</a>
                            </li>
                            <li class="login dropdown">
                                <a  class="dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="icon-man-people-streamline-user"></i> Rajan Punchouty
                                    <b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="${request.contextPath}/profile">Profile</a>
                                    </li>
                                    <li>
                                        <a href="${request.contextPath}/password/change">Change Password</a>
                                    </li>
                                    <li>
                                        <a href="${request.contextPath}/signout">Logout</a>
                                    </li>
                                </ul>
                            </li>
                            </n:isLoggedIn>
                            <n:isNotLoggedIn>
                            <li class="login js-login">
                                <a href="${request.contextPath}/signin">Login</a>
                            </li>                            
                            </n:isNotLoggedIn>
                          
                            

                        </ul>
                    </div>
                </div>
                <!-- /END CONTAINER -->
            </div>
            <!-- /END STICKY NAVIGATION -->
            
        