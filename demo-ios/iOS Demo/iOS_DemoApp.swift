//
//  iOS_DemoApp.swift
//  iOS Demo
//
//  Created by Brad Patras on 4/20/25.
//

import BasicRemoteConfigs
import SwiftUI

@main
struct DemoApp: App {
	let brc = BasicRemoteConfigs(
		remoteUrl: "https://github.com/BradPatras/brc-multiplatform/raw/main/simple-config.json",
		customHeaders: .init()
	)

	var body: some Scene {
		WindowGroup {
			ContentView(brc: brc)
		}
	}
}
