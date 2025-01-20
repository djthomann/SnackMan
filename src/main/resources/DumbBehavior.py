from Utils import *
from java.util import Random
rand = Random()

def run_behavior(environment, direction, wall_collision, x, z, y):
    """
    Executes the chicken's dumb behavior based on its surroundings.
    This chicken runs towards ghosts, avoids food, and jumps when seeing other chickens.

    :param environment: A 3x3 list representing the chicken's surroundings.
    :param direction: A string representing the direction of movement.
        One of ["N", "NE", "E", "SE", "S", "SW", "W", "NW"].
    :return: A tuple (move_x, move_z, move_y, new_direction, wall_collision) representing the movement vector and direction.
    """

    # Check the current and next tile
    current_tile = get_tile(environment, 1, 1)
    next_tile = get_tile(environment, *direction_offsets[direction])

    # make sure to fall after jump 
    if y > 0:
        return move_vectors["FALL"] + (direction,) + (wall_collision,)
    
    if current_tile == "WALL":
        wall_collision = True
        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # check if there is food in the current direction and avoid it
    if next_tile == "FOOD":
        valid_directions = [new_direction for new_direction in alternatives[direction]
                            if get_tile(environment, *direction_offsets[new_direction]) != "FOOD"]

        if valid_directions:
            new_direction = valid_directions[int(rand.nextInt(len(valid_directions)))]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

    # "Chase" ghosts
    # if a ghost is found in the surrondings, move towards it 
    for dir_key, offset in direction_offsets.items():
            tile = get_tile(environment, *offset)
            if tile == "GHOST":
                new_direction = dir_key
                return move_vectors[new_direction] + (new_direction,) + (False,)

    # If chicken sees another chicken, it jumps happy
    if next_tile == "CHICKEN":
        return move_vectors["JUMP"] + (direction,) + (wall_collision,)

    # If there is a wall, choose a random valid direction
    if next_tile == "WALL":

        if rand.nextInt(3) == 0: # 1/3 Percentage for repeated running into a wall
            return move_vectors[direction] + (direction,) + (True,)
        
        valid_directions = [new_direction for new_direction in alternatives[direction]
                            if get_tile(environment, *direction_offsets[new_direction]) != "WALL"]

        if valid_directions:
            new_direction = valid_directions[int(rand.nextInt(len(valid_directions)))]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

    # If nothing special, continue in the current direction
    return move_vectors[direction] + (direction,) + (wall_collision,)
