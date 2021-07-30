ES8 {
	classvar <options, <jack_outs, <name;

	* options {|serverOptions, jack_name = "ES8", limit=8|
		// set limit to 8 to exclude optical outs

		var inputs=0, outputs=0, options, type, property, count, num;

		jack_outs = [];

		name = jack_name;

		count = {|type| // count inputs and outputs
			(type == \input).if ({
				inputs = inputs +1;
			}, {
				(type == \output).if ({
					outputs = outputs +1;
				})
			});
		};

		serverOptions.isNil.if({ // get default options if none specified
			options = Server.default.options;
		}, {
			options = serverOptions;
		});

		SCJConnection.getproperties.keys.do({|key|
			key.postln;
			property = SCJConnection.properties[key];
			//property.postln;
			type = property[0].asSymbol; // type is input or output
			//"^ES8.*".matchRegexp(key.asString).if({ // could have been .beginswith
			key.asString.beginsWith(jack_name).if({ // is this the module we want?

				num = key.asString.replace((jack_name++":playback_"),"");
				num = num.asInteger;
				(num <= limit) .if({ // maybe don't count the optical ports
					count.(type);
					jack_outs = jack_outs.add(key);
				})
			} , {
				count.(type);
			});

			//type.postln;

		});
		options.numInputBusChannels = outputs; // we get input from outputs
		options.numOutputBusChannels = inputs;
		^options;
	}

	* disconnectSC{ |shouldBoot = false, scName = "SuperCollider:out_"|
		var src_key, func;

		func = {

			SCJConnection.getallports;
			SCJConnection.getconnections.keys.do({|src|
				var dest_keys=[];
				var src_keys=[];

				src.asString.beginsWith(scName).if({
					src_key = SCJConnection.allports.findKeyForValue(src);
					SCJConnection.connections[src].do({|dest|
						dest_keys = dest_keys.add(SCJConnection.allports.findKeyForValue(dest));
						src_keys = src_keys.add(src_key);
					});
					SCJConnection.disconnect(src_keys, dest_keys);
				});
			});
		};

		shouldBoot.if({
			s.waitForBoot(func);
		}, {
			func.value;
		});
	}

	*connectSC { |shouldBoot = false, scName = "SuperCollider:out_", jack_name|
		var func;

		jack_name.notNil.if({
			name = jack_name;
		});

		func = {

			// this one is coming

		}

	}


}
