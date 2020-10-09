extends Area2D

func _ready():
	pass


func _on_SpikeTop_body_entered(body):
	if body.has_method("hurt"):
		get_tree().call_group("Gamestate", "hurt")
	
