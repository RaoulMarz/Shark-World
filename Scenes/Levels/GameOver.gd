extends Control


func _on_RestartButton_pressed():
	load_level_1()
	
func _process(delta):
	if Input.is_action_pressed("ui_select"):
		load_level_1()

func load_level_1():
	get_tree().change_scene("res://Scenes/Levels/Level1.tscn")