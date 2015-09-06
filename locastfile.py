from locust import HttpLocust, TaskSet, task

class UserBehavior(TaskSet):
    def on_start(self):
	pass
    @task(1)
    def posts(self):
        self.client.get("posts")

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait=100
    max_wait=500
