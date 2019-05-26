// Rocket Test Stand
engine_mount = true;
engine_od = 18;
engine_length = 70;

cell_length = 80;
cell_width = 12.7;
cell_height = 12.7;
mount_bore = 4;
mount_spacing = 15;

printer_nozzle_od = 0.4;
wall = 2.44; // thickness of printed walls
$fn = 90;

module load_cell() {
    translate([(-cell_length / 2) + (mount_spacing / 2) + 7, 0, ((cell_height + printer_nozzle_od) / 2) + 4]) {
        cube([80, cell_width + printer_nozzle_od, cell_height + 0.2], center = true);
        translate([0, -(cell_width + printer_nozzle_od)/2 - 2, -(cell_height + 0.2) / 2]) cube([40, 2, cell_height + 0.2]);
    }
}


difference() {
    union() {
        if (engine_mount == true) {
            translate([-cell_length + mount_spacing + mount_bore + 8, 0 ,0]) mirror([1, 0, 0])
            difference() {
                union() {
                    cylinder(d = mount_spacing + mount_bore + 5, h = cell_height + 6);
                    translate([0, 0, cell_height + 4])
                        difference() {
                            cylinder(d = engine_od + wall + printer_nozzle_od, h = engine_length - 8);
                            translate([0, 0, 20]) {
                                cylinder(d = engine_od + printer_nozzle_od, h = engine_length);
                            }
                            
                            translate([0, 0, 19]) {
                                hull() {
                                    cylinder(d = engine_od + printer_nozzle_od - 4.5, h = 1);
                                translate([(engine_od + wall + printer_nozzle_od) / 2, 0, - (engine_od + printer_nozzle_od) / 2]) rotate([0, 90, 0]) cylinder(d = engine_od + printer_nozzle_od - 4.5, h = 1);
                                }
                            }
                        };
                };
                translate([7.5, 0, 0]) cylinder(d = 4 + printer_nozzle_od, h = 5);
                translate([-7.5, 0, 0]) cylinder(d = 4 + printer_nozzle_od, h = 5);
            }
        }
        
        hull() {
            translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
               translate([0, 2 + wall, 0]) 
                    cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = 2 * cell_height + 12);
                translate([0, -2 - wall - 10 - wall, 0]) 
                    cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = 2 * cell_height + 12);
                
                translate([-15, 2 + wall, 0]) 
                    cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = 2 * cell_height + 12);
                translate([-15, -2 - wall - 10 - wall, 0]) 
                    cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = 2 * cell_height + 12);
            }
        }
        hull() {
            translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
                translate([-15, 0, 0]) 
                    cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = cell_height + 12);
                translate([-cell_length + 10 + 15 + cell_width, 0, 0 ]) 
                    cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
            }
        }
        hull() {
            translate([30, 0, 4 - 12 - cell_height ]) 
                cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
            translate([12.5 - 5, 0, 4 - 12 - cell_height]) 
                cylinder(d1 = 13 + (wall * 2), d2 = cell_width - printer_nozzle_od, h = cell_height + 12);
        }
        hull() {
            translate([30, 0, 4 - 12 - cell_height ]) 
                cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
            translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
                    translate([-cell_length + 10, 0, 0 ]) 
                        cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
                }
            }
        translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
            translate([-cell_length + 10, 0, 0 ]) {
                hull() {
                    cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
                    rotate([0, 0, 60]) translate([-2/3 * cell_length, 0 , 0])
                        cylinder(d = cell_width - printer_nozzle_od, h = cell_height / 2);
                }
                hull() {
                    cylinder(d = cell_width - printer_nozzle_od, h = cell_height);
                    rotate([0, 0, -60]) translate([-2/3 * cell_length, 0 , 0])
                        cylinder(d = cell_width - printer_nozzle_od, h = cell_height / 2);
                }
            }
        }
    }
    
    // Mounting holes
    translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
        translate([-cell_length + 10, 0, 0 ]) {
            rotate([0, 0, 60]) translate([-2/3 * cell_length, 0 , 0]){
                cylinder(d = 5, h = cell_height * 2);
                translate([0, 0, cell_height / 2]) cylinder(d = cell_width - printer_nozzle_od, h = cell_height * 2);
            }
            rotate([0, 0, -60]) translate([-2/3 * cell_length, 0 , 0]){
                cylinder(d = 5, h = cell_height * 2);
                translate([0, 0, cell_height / 2]) cylinder(d = cell_width - printer_nozzle_od, h = cell_height * 2);
            }
        }
    }
    // Rear mount
    translate([30, 0, 4 - 12 - cell_height ]) {
        cylinder(d = 5, h = cell_height * 2);
        translate([0, 0, cell_height]) cylinder(d = cell_width - printer_nozzle_od, h = cell_height * 2);
    }
    // M5 holes
    translate([12.5 - 5, 0, 4 - 12 - cell_height]) {
        cylinder(d = 5, h = cell_height * 3);
        cylinder(d = 13, h = 6);
        translate([-15, 0, 0]) {
            cylinder(d = 5, h = cell_height * 3);
            cylinder(d = 13, h = 6);
        }
    }
    
    translate([-12.5,  -(cell_width / 2) - 2 - wall - 1 - 6, -5]) cube([28, 2, 16]);
    translate([-12.5,  -(cell_width / 2) - wall - 1 - 6, -4.5]) cube([28, 6, 15]);
    translate([-12.5,  -(cell_width / 2) - wall - 1 - 7.5 - 2, -4.5]) cube([28, 1.5, 15]);
    translate([-3,  -(cell_width / 2) - wall - 2, -4.5]) cube([15, wall, 15]);
    
    load_cell();
}
