gl = undefined
circle = {}
cube = {}
eye = {}
smile = {}
mask = {}
shaderProgram = {}
pMatrix = mat4.create()
mvMatrix = mat4.create()
mvMatrixStack = []
rTri = 0
rSquare = 0

log = (message) ->
    #alert(message)

initIndicesBuffer = (indices) ->
    buff = gl.createBuffer()
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, buff)
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indices), gl.STATIC_DRAW)
    buff.numItems = indices.length
    buff

initBuffer = (verts, itemSize, numItems) ->
    buff = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, buff)
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(verts), gl.STATIC_DRAW)
    buff.itemSize = itemSize
    buff.numItems = numItems
    buff

retellPosition = ->
    gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, mvMatrix)

setColor = (r,g,b) ->
    gl.uniform4f(shaderProgram.vColorUniform, r,g,b, 1)

changeBuffer = (buff) ->
    gl.bindBuffer(gl.ARRAY_BUFFER, buff)
    gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, buff.itemSize, gl.FLOAT, false, 0, 0)

mvPushMatrix = ->
    copy = mat4.create()
    mat4.set(mvMatrix, copy)
    mvMatrixStack.push(copy)

mvPopMatrix = ->
    if mvMatrixStack.length == 0
        throw "Empty mvMatrixStack!"
    mvMatrix = mvMatrixStack.pop()

degToRad = (degrees) ->
    return degrees * Math.PI / 180

withRotation = (degs, f) ->
    mvPushMatrix()
    mat4.rotate(mvMatrix, degToRad(degs), [0, 1, 0])
    f()
    mvPopMatrix()


initGL = (canvas) ->
    try
        gl = canvas.getContext("experimental-webgl")
        gl.viewportWidth = canvas.width
        gl.viewportHeight = canvas.height
    finally
        if not gl then log("Can't init WebGL")

getShader = (id) ->
    shaderScript = document.getElementById(id)
    return null if not shaderScript

    str = ""
    k = shaderScript.firstChild
    while k
        if k.nodeType == 3
            str += k.textContent
        k = k.nextSibling

    shader = {}
    if shaderScript.type == "x-shader/x-fragment"
        shader = gl.createShader(gl.FRAGMENT_SHADER)
    else if shaderScript.type == "x-shader/x-vertex"
        shader = gl.createShader(gl.VERTEX_SHADER)
    else
        return null
    gl.shaderSource(shader, str)
    gl.compileShader(shader)

    if not gl.getShaderParameter(shader, gl.COMPILE_STATUS)
        log(gl.getShaderInfoLog(shader))
        return null
    shader


initShaders = ->
    fragmentShader = getShader("shader-fs")
    vertexShader = getShader("shader-vs")
    shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, vertexShader)
    gl.attachShader(shaderProgram, fragmentShader)
    gl.linkProgram(shaderProgram)

    if not gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)
        log("Could not initialize shaders")

    gl.useProgram(shaderProgram)

    shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition")
    gl.enableVertexAttribArray(shaderProgram.vertexPositionAttribute)

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")
    shaderProgram.vColorUniform = gl.getUniformLocation(shaderProgram, "vColor")


initCircle = (r, density)->
    verts = ([r * Math.cos(t), 0.0, r * Math.sin(t)] for t in (
                   (Math.PI * 2 * i) / density for i in [0...density])).reduce(
                        (prev, curr, index, array) -> prev.concat(curr))
    initBuffer(verts, 3, verts.length / 3)


init = ->
    canvas = document.getElementById('robot_canvas')
    initGL(canvas)
    initShaders()

    circle = initCircle(9, 50)
    eye = initCircle(0.15, 8)

    smile = initBuffer([
        0, 0, -0.3,
        0, -0.2, -0.1,
        0, -0.2, 0.1,
        0, 0, 0.3
    ], 3, 4)

    mask = initBuffer([
        0, 0.5, 0.5,
        0, 0.5, -0.5,
        0, -0.5, 0.5,
        0, -0.5, -0.5
    ], 3, 4)

    cube = initBuffer([
        -1, 1, 1,
        -1, -1, 1,
        1, 1, 1,
        1, -1, 1,
        1, 1, -1,
        1, -1, -1,
        -1, 1, -1,
        -1, -1, -1
    ], 3, 8)

    cube.indices = initIndicesBuffer([0,1,2,3,4,5,6,7, 0,2,1,3,4,6,5,7, 0,6,2,4,3,5,1,7])

    gl.clearColor(0,0,0,1)
    gl.enable(gl.DEPTH_TEST)

rotateZ = (deg) ->
    mat4.rotate(mvMatrix, degToRad(deg), [0,0,1])

rotateY = (deg) ->
    mat4.rotate(mvMatrix, degToRad(deg), [0,1,0])

rotateX = (deg) ->
    mat4.rotate(mvMatrix, degToRad(deg), [1,0,0])

translate = (x,y,z) ->
    mat4.translate(mvMatrix, [x,y,z])

scale = (x,y,z) ->
    mat4.scale(mvMatrix, [x,y,z])

drawFace = (happy) ->
    #glPushMatrix();
    mvPushMatrix()
    #glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    #glTranslatef(-0.5f, 0.25f, 0.25f);
    translate(-0.5, 0.25, 0.25)
    rotateZ(90)
    retellPosition()
    #glColor3f(0, 1, 1);
    setColor(0,1,1,1)
    changeBuffer(eye)
    #sphere.draw(0.15f, 10, 10);
    gl.drawArrays(gl.TRIANGLE_FAN, 0, eye.numItems)
    #glTranslatef(0, 0, -0.5f);
    translate(0,0,-0.5)
    retellPosition()
    #sphere.draw(0.15f, 10, 10);
    gl.drawArrays(gl.TRIANGLE_FAN, 0, eye.numItems)
    rotateZ(-90)
    #glTranslatef(0, -0.25f, 0.25f);
    #glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    #glTranslatef(0.15f, 0,0);
    translate(0.15, -0.25, 0.25)
    retellPosition()
    #glColor3f(0,0,0);
    setColor(0,0,0,1)
    #glInterleavedArrays(GL_V3F, 0, mask_buff);
    changeBuffer(mask)
    #glDrawArrays(GL_QUAD_STRIP, 0, 4);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, mask.numItems)
    #
    if not happy
        #    glRotatef(180, 1,0,0);
        rotateX(180)
        #    glTranslatef(0,0.3f,0);
        translate(0, 0.3, 0)
    #}
    #glTranslatef(-0.15f, -0.05f,0);
    translate(-0.15, -0.05, 0)
    #glColor3f(1, 0, 0);
    setColor(1,0,0,1)
    gl.lineWidth(3)
    retellPosition()
    changeBuffer(smile)
    gl.drawArrays(gl.LINE_STRIP, 0, smile.numItems)
    gl.lineWidth(1)

    #glPopMatrix();
    mvPopMatrix();

drawCube = (size) ->
    mvPushMatrix()
    size = size / 2
    scale(size, size, size)
    retellPosition()
    gl.drawElements(gl.LINES, cube.indices.numItems, gl.UNSIGNED_SHORT, 0)
    mvPopMatrix()

drawCubeWithSwing = (size, length, swing) ->
    rotateY(swing)
    mvPushMatrix()
    scale(length / size, 1,1)
    drawCube(size)
    mvPopMatrix()
    translate(length / 2 - size / 2, 0, 0)

drawCubeForward = (size, length) ->
    translate(length / 2 + size, 0,0)
    drawCubeWithSwing(size, length, 0)

drawCubeBackward = (size, length) ->
    translate(length / 2 - size / 2, 0,0)
    mvPushMatrix()
    scale(length / size, 1,1)
    drawCube(size)
    mvPopMatrix()
    translate(length / 2 + size, 0,0)

drawBot = (degs, happy) ->
    lastIdx = degs.length-1
    [a, b, c, d, swing, z] = (degs[x] for x in (if degs[lastIdx] == 1 then [0,1,2,3,4,lastIdx] else [2,3,0,1,4,lastIdx]))
    mvPushMatrix()
    translate(0,0,z)
    drawCubeWithSwing(0.3, 1, swing)
    rotateZ(180 - a)
    drawCubeForward(0.3, 2)
    rotateZ(b - 180)
    drawCubeForward(0.3, 2)
    rotateZ(a - b)
    translate(0,0,-z)
    drawCube(1)
    drawFace(happy)
    changeBuffer(cube)
    setColor(1,1,1,1)
    translate(0,0,-z)
    rotateZ(d - c - 180)
    drawCubeBackward(0.3, 2)
    rotateZ(180 - d)
    drawCubeBackward(0.3, 2)
    rotateZ(c - 180)
    drawCubeBackward(0.3, 1)
    mvPopMatrix()

sadBotTerm = 15000

sadBot = ->
    mvPushMatrix()
    scale(2,2,2)
    rot = -((new Date().getTime() - start) % sadBotTerm) * 360 / sadBotTerm
    rotateY(rot)
    drawBot([0,90, 0, 90, 0,0,-1], false)
    mvPopMatrix()


leftDeg = 11.25;
rightDeg = 10.18;

sequence = [[90, 120, 30, 120, -leftDeg, 0, 1],
            [76, 180, 30, 60, 0, leftDeg, 1],
            [30, 120, 90, 120, -rightDeg, 0, -1],
            [30, 60, 90, 180, 0, rightDeg, -1]]

seqlength = sequence[0].length

start = new Date().getTime()
last_i = 0
rotation = 180
rotStep = 21.43
term = 1000
botFigureInTime = () ->
    moment = (((new Date().getTime() - start) % term) * sequence.length) / term
    i = Math.floor(moment)
    i2 = (i + 1) % sequence.length
    amount = moment - i
    degs = []
    for d in [0...4]
        degs[d] = sequence[i][d] * (1 - amount) + sequence[i2][d] * amount

    degs[4] = sequence[i][4] * (1 - amount) + amount * sequence[i][5]
    degs[seqlength - 1] = sequence[i][seqlength - 1];

    if sequence[i][seqlength - 1] != sequence[last_i][seqlength - 1]
        rotation += rotStep;
    last_i = i;
    degs


drawScene = ->
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight)
    gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, pMatrix)
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT)
    mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0, pMatrix)
    mat4.identity(mvMatrix)

    # moving away from eye
    mat4.translate(mvMatrix, [0.0,-2.8,-18.0])
    retellPosition()
    setColor(0,1,0)

    # drawing circles on the floor
    changeBuffer(circle)
    gl.drawArrays(gl.LINE_LOOP, 0, circle.numItems)
    mvPushMatrix()
    scale(11/9, 1, 11/9)
    retellPosition()
    gl.drawArrays(gl.LINE_LOOP, 0, circle.numItems)
    mvPopMatrix()

    # will paint some cubes
    setColor(1,1,1)
    changeBuffer(cube)

    sadBot()

    figure = botFigureInTime()
    rotateY(rotation)
    for i in [0...8]
        rotateY(45)
        mvPushMatrix()
        translate(10, 0.15, 0)
        rotateY(-90)
        drawBot(figure, true)
        mvPopMatrix()
    gl.flush()

tick = ->
    requestAnimationFrame(tick)
    drawScene()

init()
tick()
