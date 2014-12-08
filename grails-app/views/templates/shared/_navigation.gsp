		<%@ page import="grails.plugin.nimble.core.AdminsService"%>	
		<%@ page import="org.apache.shiro.SecurityUtils"%>	
		<link href='http://fonts.googleapis.com/css?family=Cabin:400,600|Open+Sans:300,600,400' rel='stylesheet'>
			
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
                                    <i class="icon-man-people-streamline-user"></i> ${currentUser.profile.fullName}
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
                            <li class="register-button js-register inpage-scroll">
                                <a href="${request.contextPath}/signup" class="navbar-register-button">Sign Up</a>
                            </li>
                            </n:isNotLoggedIn>

                        </ul>
                    </div>
                </div>
                <!-- /END CONTAINER -->
            </div>
            <!-- /END STICKY NAVIGATION -->
    	

   