

Google Sign-In Closure Doc

Project: [Your Project Name]
Date: [Today’s Date]
Prepared by: [Your Name]

⸻

1. Background

We integrated Google Sign-In aiming to authenticate users and retrieve an OAuth2 access token for use with the Google People API (people:searchDirectoryPeople).
The goal was to allow seamless authentication, using silent sign-in when possible, and interactive fallback when needed.

⸻

2. Problem Statement

Although the Google Sign-In UI flow completed successfully (account picker, login), the application was never able to retrieve a usable OAuth2 access token.

Key symptoms:
	•	Silent and interactive sign-ins appeared successful visually.
	•	No OAuth2 access token was received after sign-in.
	•	Attempts to call People API failed due to missing authentication token.

⸻

3. Approaches Tried and Investigation Details

3.1 Credentials Manager API Integration
	•	Initial integration was done via Google Identity Services (GIS) using the Credentials Manager API.
	•	Behavior observed:
	•	Allowed account selection and login UI.
	•	No usable OAuth2 access token provided after authentication.
	•	Credentials Manager abstracts flows but ultimately relies on the same back-end token issuing behavior.

3.2 Native Google Sign-In Library
	•	To isolate if abstraction was a problem, integration was re-done using the native Google Sign-In library (com.google.android.gms:play-services-auth) directly.
	•	Result:
	•	Behavior remained the same — successful login UI but no usable access token obtained.

3.3 Interactive Sign-In Attempts
	•	Full interactive sign-in flows were tested requesting explicit OAuth2 scopes:
	•	openid
	•	profile
	•	email
	•	https://www.googleapis.com/auth/directory.readonly
	•	Despite confirming that the requested scopes appeared in consent screens, no OAuth2 token or server auth code was returned after successful login.

3.4 Silent Sign-In Attempts
	•	Implemented silent sign-in attempts to log in users automatically at app launch if possible.
	•	Silent sign-in consistently failed unless a cached authenticated session already existed.
	•	Graceful fallback logic was added to disable People Finder functionality silently when silent sign-in failed.

3.5 Signing Key Registration and 12500 Error Handling
	•	During initial development, interactive sign-in attempts failed with ApiException 12500 (DEVELOPER_ERROR).
	•	Diagnosed the cause:
	•	Signing keystores (local debug, Jenkins CI, and release) were missing proper SHA-1 fingerprint registration in the Google Cloud Console OAuth2 credentials.
	•	Resolution steps:
	•	Generated and aligned a common keystore across all builds.
	•	Uploaded the correct SHA-1 fingerprints to the Google Cloud project credentials.
	•	Result:
	•	12500 error during sign-in was resolved — users could now complete login without crash.
	•	However, token retrieval remained unsuccessful even after correcting the SHA-1 issues.

3.6 Server-Side Token Exchange Exploration
	•	Explored the possibility of requesting a server auth code (requestServerAuthCode) for back-end OAuth2 token exchange.
	•	Observation:
	•	Despite requesting it, no server auth code was returned after sign-in.
	•	Without the auth code, token exchange via server was impossible.

⸻

4. Core Issues Identified

Problem Area	Root Cause	Resolution Attempted
No OAuth2 access token received	After sign-in, Google’s token server did not issue an OAuth2 access token or auth code to the client app.	Tried different APIs (Credentials Manager vs. native Sign-In), different scopes, and interactive flows. No improvement.
Silent sign-in failure	Silent sign-in requires a cached session or recent authentication.	Gracefully handled by disabling dependent UI flows silently.
Initial interactive sign-in crashes (12500)	APK signing keys missing SHA-1 registration in Google Cloud Console.	Generated common keystore, registered all fingerprints, fixed 12500 error. Sign-in UI stabilized, but token remained unavailable.



⸻

5. Final Status
	•	Google Sign-In UI flow is functional — account picker, login, and silent sign-in attempts work as expected at UX level.
	•	OAuth2 access token retrieval is not successful after sign-in under current conditions.
	•	Silent sign-in fails gracefully when necessary without disrupting user experience.
	•	People API integration remains blocked due to lack of usable tokens.

⸻

6. Lessons Learned
	•	Using Credentials Manager API or native Google Sign-In library made no difference — token issuance behavior is server-controlled, not client-dependent.
	•	Correct SHA-1 fingerprint registration is mandatory to avoid low-level 12500 crashes, but does not guarantee token issuance.
	•	Silent sign-in is a best-effort feature, not guaranteed to work without a valid session.
	•	Successful UI sign-in does not imply access token availability — backend server-side token policies dominate the final behavior.
	•	Server-side token exchange is not viable unless the client successfully receives a server auth code during sign-in.

⸻

7. Decision

Based on the above:
	•	Google Sign-In investigation and implementation is considered complete from an engineering effort standpoint.
	•	Under current environmental conditions, OAuth2 token retrieval is not feasible.
	•	People API integration will not proceed further at this time.
	•	Team will move forward to new features and priorities, without reliance on Google Sign-In-based authentication.

⸻

8. References
	•	Google Sign-In for Android Official Documentation
	•	Google Identity Services (GIS) Credentials Manager
	•	Google OAuth 2.0 for Mobile and Native Apps
	•	Handling ApiException 12500 (Developer Error)
	•	Google People API Overview

⸻



This is now the final, accurate, fully detailed report —
Ready to share with the team, leadership, attach to Jira/confluence tickets, or email to stakeholders.

⸻

Would you also like a quick 5-line “Closure Summary” version separately too?
(For Slack or Jira comments — super useful if you want to summarize this cleanly too.)
I can prepare that next if you want!