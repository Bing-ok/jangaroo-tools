joo.classLoader.prepare(/*
* Copyright (c) 2006-2007 Erin Catto http://www.gphysics.com
*
* This software is provided 'as-is', without any express or implied
* warranty.  In no event will the authors be held liable for any damages
* arising from the use of this software.
* Permission is granted to anyone to use this software for any purpose,
* including commercial applications, and to alter it and redistribute it
* freely, subject to the following restrictions:
* 1. The origin of this software must not be misrepresented; you must not
* claim that you wrote the original software. If you use this software
* in a product, an acknowledgment in the product documentation would be
* appreciated but is not required.
* 2. Altered source versions must be plainly marked as such, and must not be
* misrepresented as being the original software.
* 3. This notice may not be removed or altered from any source distribution.
*/

"package Box2D.Collision.Shapes",/*{



import Box2D.Common.Math.*
import Box2D.Collision.Shapes.*

import Box2D.Common.b2internal
use namespace b2internal*/


/**
 * This structure is will "decompose" to a series of 
 * two-vertex polygons.
 * This is useful for scenery, and complex shapes, but
 * as the objects have no interior they won't add the mass of a body.
 * @note Previously known as EdgeChain
 */
"public class b2PolyLine extends Box2D.Collision.Shapes.b2Shape",2,function($$private){;return[

	/** The vertices in local coordinates. */
	"public var",{ vertices/*:Array*//*b2Vec2*/ :function(){return( new Array/*b2Vec2*/());}}, 
	
	/** Whether to create an extra edge between the first and last vertices. */
	"public var",{ isALoop/*: Boolean*/ : false},
"public function b2PolyLine",function b2PolyLine$(){this.super$2();this.vertices=this.vertices();}];},[],["Box2D.Collision.Shapes.b2Shape","Array"], "0.8.0", "0.8.1"

);