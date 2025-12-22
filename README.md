# WebBasedMatsim
这个项目由GZPI交通模型团队开发并免费发布，目的是为降低MATSim建模的门槛，对于稍微复杂一些的场景，MATSim通常要么需要详实的数据支撑，要么需要繁复的配置和二次开发。为此我们开发了尽量简单的图形界面，对于一般的交通工程师可以通过拖拽的方式快速建立MATSim场景，而对于高阶用户来说，可以上传自己的输入，利用服务器的算力快速完成模型的计算。项目集成了ActivitySim与UrbanSim相关模块用于活动出行链和，用户可以使用不同地区的模版直接生成当地的输入模型，也可以在此基础上详细配置自己所需的参数。用户可以自己搭建本地服务器使用相关功能，也可以访问：www.matsim.cn
体验或建立自己所需的模型。对于大型场景的建模，未来规划用户可自行配置所需的内存与cpu资源，自动购买阿里云算力解决模型计算问题。
项目主要用于MATSim的拖拽式建模和模型的可视化，ActivitySim与UrbanSim主要是为方便输入和场景应用涉及到的预测，其中MATSim涉及到的部分后端是基于JAVA，ActivitySim、UrbanSim基于python。欢迎用户提供本地成熟的ActivitySim
与UrbanSim数据模版，另外如GTFS或用户建立的模型与可以公开在样例中展示。以及在建模过程中如果遇到任何bug和问题，或者需要专业的建模团队帮助建立本地模型，y请联系xxx，matsimChina.gzpi.


This project is a web-based MATSim tool to build basic scenarios for beginners as well as advanced users to build fast MATSim models.
The whole project is developed via Intellij Idea, the framework is Springboot 3 with myBatis. It uses MATSim 2025.0 for development.
detailed configs and documents are coming soon with integration of ActivitySim and UrbanSim ~

