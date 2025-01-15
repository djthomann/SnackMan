from Utils import *
def run_behavior(environment, direction, wall_collision,x,z):
    """
    Executes the chicken's behavior based on its surroundings and the last known direction of movement.
    
    :param environment: A 3x3 list representing the chicken's surroundings.
    :param direction: A string representing the direction of movement.
        One of ["N", "NE", "E", "SE", "S", "SW", "W", "NW"].
    :return: A tuple (move_x, move_z, dz, new_direction) representing the movement vector and direction.
    """

    # Check the tile in the current direction and the next Tile
    current_tile = get_tile(environment, 1, 1)
    next_tile = get_tile(environment, *direction_offsets[direction])
    
    # If the current direction is not blocked and has no food in it, check for food right or left
    if current_tile != "WALL" and next_tile != "FOOD" and wall_collision == False:
        if ((direction == "N" or direction == "S") and (x % 1.0 > 0.49 and x % 1.0 < 0.51)) or ((direction == "E" or direction == "W") and (z % 1.0 > 0.49 and z % 1.0 < 0.51)):
            valid_directions = []
            for new_direction in alternatives[direction]:
                if get_tile(environment,*direction_offsets[new_direction]) == "FOOD":
                    valid_directions.append(new_direction)
            if valid_directions:
                new_direction = random.choice(valid_directions)
                return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
            else:
                return move_vectors[direction] + (direction,) + (wall_collision,)
        
    # If the current direction is not blocked and has food in it, continue moving in that direction
    if current_tile != "WALL" and wall_collision == False: 
        return move_vectors[direction] + (direction,) + (wall_collision,)
    
    # If the current direction is blocked, turn around and set wall_collision to True , should not happen!
    if current_tile == "WALL":
        wall_collision = True
        return move_vectors[opposite_directions[direction]] + (direction,) + (wall_collision,)

    # If the current direction is blocked, check if an alternative is available
    if wall_collision:
        valid_directions = []
        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL":
                valid_directions.append(new_direction)
        if valid_directions:
            new_direction = random.choice(valid_directions)  # Randomly select a valid direction
            wall_collision = False
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        else:
            wall_collision = False
            return move_vectors[opposite_directions[direction]] + (opposite_directions[direction],) + (wall_collision,)

    # If no alternative is available, stay in place
    return (0.0, 0.0, 0.0, direction, wall_collision)