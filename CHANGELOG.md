# Changelog

# 1.0.0-rc.3

* Change API endpoints
  * `DELETE /subscriptions/{token}` to remove a subscription
  * `POST /subscriptions/tokens` to create an unsubscription token

# 1.0.0-rc.2

* Disabled CSRF, until we have a working implementation for the clients

# 1.0.0-rc.1 

* Added first REST endpoints to enable 
  * requests to retreive a cancellation token and
  * submit the subscription cancellation
